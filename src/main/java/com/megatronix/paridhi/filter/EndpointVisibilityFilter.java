package com.megatronix.paridhi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.join;

@Component
public class EndpointVisibilityFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(EndpointVisibilityFilter.class);
	private final ObjectMapper objectMapper;
	private final Environment environment;

	// only paths starting with these strings will be accessible in Production.
	private static final List< String > LIVE_ENDPOINTS = List.of(
		"/api/auth",
		"/api/admin",
		"/api/mrd",
		"/api/profiles",
		"/api/events",
		"/api/event-combos",
		"/api/teams",
		"/api/crd",
		"/actuator"
	);

	public EndpointVisibilityFilter (
		ObjectMapper objectMapper,
		Environment environment ) {
		this.objectMapper = objectMapper;
		this.environment = environment;
	}

	@Override
	protected void doFilterInternal (
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain ) throws ServletException, IOException {
		String[] activeProfiles = environment.getActiveProfiles();
		// step-1: environment bypass
		if ( Arrays.asList(activeProfiles).contains("dev") ||
			Arrays.asList(activeProfiles).contains("test") ) {
			if ( log.isInfoEnabled() ) {
				log.info("Bypassing endpoint visibility in {} environment", join(", ", activeProfiles));
			}
			filterChain.doFilter(request, response);
			return;
		}

		String requestURI = request.getRequestURI();

		// step-2: check if the request URI starts with these URIs
		if ( requestURI.equals("/") || requestURI.equals("/favicon.ico") ) {
			filterChain.doFilter(request, response);
			return;
		}

		// step-3: check if the request URI starts with any of the LIVE_ENDPOINTS
		boolean isAllowed = LIVE_ENDPOINTS.stream().anyMatch(requestURI::startsWith);

		if ( isAllowed ) {
			filterChain.doFilter(request, response);
		} else {
			log.error("Blocked access to non-live endpoint: {}", requestURI);
			sendErrorResponse(response, requestURI);
		}
	}

	private void sendErrorResponse ( HttpServletResponse response, String path ) throws IOException {
		response.setStatus(HttpStatus.NOT_FOUND.value());
		response.setContentType("application/json");

		ErrorResponse errorResponse = ErrorResponse.of(
			HttpStatus.NOT_FOUND.value(),
			"The requested endpoint does not exist.",
			"Not Found",
			path);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
