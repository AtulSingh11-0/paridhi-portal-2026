package com.megatronix.paridhi.security;

import com.megatronix.paridhi.service.UserDetailsServiceImpl;
import com.megatronix.paridhi.util.LoggingUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.megatronix.paridhi.constant.AppConstant.Common.FAILED;
import static com.megatronix.paridhi.constant.AppConstant.Security.BEARER;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.TOKEN_VALIDATION;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.JWT_TOKEN_INVALID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Filter that intercepts every request to validate JWT tokens in the Authorization header.
 * If a valid token is present, it authenticates the user within the Spring Security context.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final JwtService jwtService;
	private final UserDetailsServiceImpl userDetailsService;

	public JwtAuthenticationFilter (
		JwtService jwtService,
		UserDetailsServiceImpl userDetailsService
	) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal (
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		final String authHeader = request.getHeader(AUTHORIZATION);
		final String jwt;
		final String userEmail;

		// step-1: check if the request has a valid authorization header, which starts with BEARER
		if ( authHeader == null || ! authHeader.startsWith(BEARER) ) {
			filterChain.doFilter(request, response);
			return;
		}

		// step-2: extract the JWT token from the authorization header using the prefix length
		jwt = authHeader.substring(BEARER.length());

		try {
			// step-3: extract the user email from the JWT token
			userEmail = jwtService.extractUsername(jwt);

			// step-4: if email is resolved and user is not already authenticated, proceed with validation
			if ( userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
				// step-4a: load user details from the database
				var userDetails = userDetailsService.loadUserByUsername(userEmail);

				// step-4b: if token is cryptographically valid and not expired, authenticate the user
				if ( jwtService.isTokenValid(jwt, userDetails) ) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
					);

					authToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request)
					);

					SecurityContextHolder.getContext().setAuthentication(authToken);

					if ( log.isTraceEnabled() ) {
						log.trace("User {} successfully authenticated via JWT", userEmail);
					}
				}
			}
		} catch ( Exception e ) {
			// step-5: handle authentication failures — log to security audit trail
			log.warn("Failed to authenticate user with JWT token: {}", e.getMessage());
			LoggingUtil.logSecurity(log, TOKEN_VALIDATION, null, FAILED, JWT_TOKEN_INVALID + ": " + e.getMessage());
		}

		// step-6: continue with the filter chain
		filterChain.doFilter(request, response);
	}
}
