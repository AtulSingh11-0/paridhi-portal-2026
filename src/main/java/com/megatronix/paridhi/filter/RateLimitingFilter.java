package com.megatronix.paridhi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megatronix.paridhi.dto.response.ErrorResponse;
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
import org.springframework.http.MediaType;
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

import static java.lang.String.join;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);
	private final Environment environment;
	private final ObjectMapper objectMapper;
	private final RateLimitingService rateLimitingService;

	private static final String VISITOR_COOKIE_NAME = "Paridhi-Visitor-Id";

	public RateLimitingFilter (
		Environment environment,
		ObjectMapper objectMapper,
		RateLimitingService rateLimitingService
	) {
		this.environment = environment;
		this.objectMapper = objectMapper;
		this.rateLimitingService = rateLimitingService;
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
			sendErrorResponse(response, request.getRequestURI(), "Too many requests. You have been temporarily blocked for API abuse.");
			return;
		}

		// step-4: resolve bucket and consume token
		Bucket bucket = rateLimitingService.resolveBucket(clientKey);
		if ( bucket.tryConsume(1) ) {
			filterChain.doFilter(request, response);
		} else {
			rateLimitingService.applyProgressivePenalty(clientKey);
			log.warn("Rate limit exceeded for key: {}. Applying penalty.", clientKey);
			sendErrorResponse(response, request.getRequestURI(), "Rate limit exceeded. Progressive penalty applied.");
		}
	}

	private String resolveClientIdentity ( HttpServletRequest request, HttpServletResponse response ) {
		// layer-1: JWT authentication
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (
			auth != null &&
				auth.isAuthenticated() &&
				! auth.getName().equals("anonymousUser")
		) {
			return "user:" + auth.getName();
		}

		// layer-2: anonymouse tracking cookie
		Cookie visitorCookie = WebUtils.getCookie(request, VISITOR_COOKIE_NAME);
		if (
			visitorCookie != null &&
				visitorCookie.getValue() != null &&
				! visitorCookie.getValue().isEmpty()
		) {
			return "visitor:" + visitorCookie.getValue();
		}

		// layer-3: fallback to (IP address + user-agent) + injecting the cookie for future tracking
		String ipAddress = request.getHeader("X-Forwarded-For");
		if ( ipAddress == null || ipAddress.isEmpty() ) {
			ipAddress = request.getRemoteAddr();
		}
		String userAgent = request.getHeader("User-Agent");
		if ( userAgent == null ) userAgent = "unknown-agent";

		// generate cookie so their next request fall into layer-2
		String newVisitorId = UUID.randomUUID().toString();
		ResponseCookie newVisitorCookie = ResponseCookie.from(VISITOR_COOKIE_NAME, newVisitorId)
			.httpOnly(true)
			.path("/")
			.maxAge(30L * 24 * 60 * 60) // 30 days expiration
			.secure(true)
			.sameSite("Lax") // adjust as needed (Strict may cause issues with some cross-origin scenarios)
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, newVisitorCookie.toString());

		return "net:" + ipAddress + "|" + userAgent;
	}

	private void sendErrorResponse ( HttpServletResponse response, String message, String path ) throws IOException {
		response.setStatus(TOO_MANY_REQUESTS.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ErrorResponse errorResponse = ErrorResponse.of(
			TOO_MANY_REQUESTS.value(),
			message,
			"Too Many Requests",
			path
		);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}