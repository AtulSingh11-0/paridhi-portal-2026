package com.megatronix.paridhi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.security.JwtService;
import com.megatronix.paridhi.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static com.megatronix.paridhi.constant.AppConstant.Security.*;
import static java.lang.String.join;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);
	private final Environment environment;
	private final ObjectMapper objectMapper;
	private final RateLimitingService rateLimitingService;
	private final JwtService jwtService;

	public RateLimitingFilter (
		Environment environment,
		ObjectMapper objectMapper,
		RateLimitingService rateLimitingService,
		com.megatronix.paridhi.security.JwtService jwtService
	) {
		this.environment = environment;
		this.objectMapper = objectMapper;
		this.rateLimitingService = rateLimitingService;
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal (
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		String[] activeProfiles = environment.getActiveProfiles();
		// step-1: environment bypass
		if (
			Arrays.asList(activeProfiles).contains("dev") ||
				Arrays.asList(activeProfiles).contains("test")
		) {
			if ( log.isInfoEnabled() ) {
				log.info("Bypassing rate limiting in {} environment", join(", ", activeProfiles));
			}
			filterChain.doFilter(request, response);
			return;
		}

		// step-2: generate 3-layer identity key
		String clientKey = resolveClientIdentity(request, response);

		// step-3: check penalty box
		if ( rateLimitingService.isBlocked(clientKey) ) {
			log.warn("Blocked request from penalized key: {}", clientKey);
			sendErrorResponse(response, request.getRequestURI());
			return;
		}

		// step-4: resolve bucket and consume token
		Bucket bucket = rateLimitingService.resolveBucket(clientKey);
		if ( bucket.tryConsume(1) ) {
			filterChain.doFilter(request, response);
		} else {
			rateLimitingService.applyProgressivePenalty(clientKey);
			log.warn("Rate limit exceeded for key: {}. Applying penalty.", clientKey);
			sendErrorResponse(response, request.getRequestURI());
		}
	}

	private String resolveClientIdentity ( HttpServletRequest request, HttpServletResponse response ) {
		String authIdentity = resolveAuthIdentity(request);
		if ( authIdentity != null ) return authIdentity;

		String visitorIdentity = resolveVisitorIdentity(request);
		if ( visitorIdentity != null ) return visitorIdentity;

		return resolveNetworkIdentity(request, response);
	}

	private String resolveAuthIdentity ( HttpServletRequest request ) {
		// layer-1: JWT authentication (Direct extraction to avoid DB hits)
		String authHeader = request.getHeader(AUTHORIZATION);
		if ( authHeader != null && authHeader.startsWith(BEARER) ) {
			try {
				String jwt = authHeader.substring(7);
				String userEmail = jwtService.extractUsername(jwt);
				if ( userEmail != null ) return USER_PREFIX + userEmail;
			} catch ( Exception e ) {
				// ignore parsing errors and fallback to other layers
				if ( log.isDebugEnabled() ) {
					log.debug("Failed to extract username from JWT: {}", e.getMessage());
				}
			}
		}

		// security context fallback
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( auth != null && auth.isAuthenticated() && ! auth.getName().equals(ANONYMOUS_USER) ) {
			return USER_PREFIX + auth.getName();
		}

		return null;
	}

	private String resolveVisitorIdentity ( HttpServletRequest request ) {
		// layer-2: anonymous tracking cookie
		Cookie visitorCookie = WebUtils.getCookie(request, VISITOR_COOKIE);
		if ( visitorCookie != null && visitorCookie.getValue() != null && ! visitorCookie.getValue().isEmpty() ) {
			return VISITOR_PREFIX + visitorCookie.getValue();
		}

		return null;
	}

	private String resolveNetworkIdentity ( HttpServletRequest request, HttpServletResponse response ) {
		// layer-3: fallback to (IP address + user-agent) + injecting the cookie for future tracking
		String ipAddress = request.getHeader(X_FORWARDED_FOR);
		if ( ipAddress == null || ipAddress.isEmpty() ) {
			ipAddress = request.getRemoteAddr();
		}
		String userAgent = request.getHeader(USER_AGENT);
		if ( userAgent == null ) userAgent = UNKNOWN;

		// generate cookie so their next request fall into layer-2
		String newVisitorId = UUID.randomUUID().toString();
		ResponseCookie newVisitorCookie = ResponseCookie.from(VISITOR_COOKIE, newVisitorId)
			.httpOnly(true)
			.path("/")
			.maxAge(30L * 24 * 60 * 60) // 30 days expiration
			.secure(true)
			.sameSite("Lax")
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, newVisitorCookie.toString());

		return NET_PREFIX + ipAddress + "|" + userAgent;
	}

	private void sendErrorResponse ( HttpServletResponse response, String path ) throws IOException {
		response.setStatus(TOO_MANY_REQUESTS.value());
		response.setContentType(APPLICATION_JSON_VALUE);

		ErrorResponse errorResponse = ErrorResponse.of(
			TOO_MANY_REQUESTS.value(),
			MessageConstant.UserMessage.TOO_MANY_REQUESTS,
			TOO_MANY_REQUESTS.name(),
			path
		);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}