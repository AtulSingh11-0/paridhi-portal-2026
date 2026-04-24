package com.megatronix.paridhi.exception;

import com.megatronix.paridhi.dto.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class SecurityExceptionHandler {

	/************************************************************** UNAUTHORIZED EXCEPTION's **************************************************************/

	@ExceptionHandler ( BadCredentialsException.class )
	public ResponseEntity<ErrorResponse> handleBadCredentialsException (
		BadCredentialsException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password: " + exception.getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	@ExceptionHandler ( AuthenticationException.class )
	public ResponseEntity<ErrorResponse> handleAuthenticationException (
		AuthenticationException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), "Authentication failed: " + exception.getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	@ExceptionHandler ( JwtException.class )
	public ResponseEntity<ErrorResponse> handleJwtException (
		JwtException exception,
		HttpServletRequest request
	) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String message = "Invalid token";

		if ( exception instanceof ExpiredJwtException ) {
			message = "Token has expired";
		}

		ErrorResponse errorResponse = ErrorResponse.of(status.value(), message, status.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(status).body(errorResponse);
	}

	/************************************************************** FORBIDDEN EXCEPTION's **************************************************************/

	@ExceptionHandler ( RegistrationClosedException.class )
	public ResponseEntity<ErrorResponse> handleRegistrationClosedException (
		RegistrationClosedException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.value(), exception.getMessage(), HttpStatus.FORBIDDEN.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	@ExceptionHandler ( AccessDeniedException.class )
	public ResponseEntity<ErrorResponse> handleAccessDeniedException (
		AccessDeniedException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.value(), "You don't have permission to access this resource: " + exception.getMessage(), HttpStatus.FORBIDDEN.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	@ExceptionHandler ( ForbiddenAccessException.class )
	public ResponseEntity<ErrorResponse> handleForbiddenAccessException (
		ForbiddenAccessException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.value(), exception.getMessage(), HttpStatus.FORBIDDEN.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

}
