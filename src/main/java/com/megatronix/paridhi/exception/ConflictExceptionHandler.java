package com.megatronix.paridhi.exception;

import com.megatronix.paridhi.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(3)
@RestControllerAdvice
public class ConflictExceptionHandler {

	@ExceptionHandler ( DuplicateResourceException.class )
	public ResponseEntity<ErrorResponse> handleDuplicateResourceException (
		DuplicateResourceException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(), exception.getMessage(), HttpStatus.CONFLICT.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler ( UserAlreadyExistsException.class )
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException (
		UserAlreadyExistsException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(), exception.getMessage(), HttpStatus.CONFLICT.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler ( UserAlreadyVerifiedException.class )
	public ResponseEntity<ErrorResponse> handleUserAlreadyVerifiedException (
		UserAlreadyVerifiedException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(), exception.getMessage(), HttpStatus.CONFLICT.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler ( ProfileAlreadyExistsException.class )
	public ResponseEntity<ErrorResponse> handleProfileAlreadyExistsException (
		ProfileAlreadyExistsException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(), exception.getMessage(), HttpStatus.CONFLICT.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler ( DataIntegrityViolationException.class )
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException (
		DataIntegrityViolationException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(), "Data integrity violation: Operation cannot be performed: " + exception.getMessage(), HttpStatus.CONFLICT.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

}
