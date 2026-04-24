package com.megatronix.paridhi.exception;

import com.megatronix.paridhi.dto.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(2)
@RestControllerAdvice
public class NotFoundExceptionHandler {

	@ExceptionHandler ( EntityNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException (
		EntityNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( EventNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleEventNotFoundException (
		EventNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( GIDNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleGIDNotFoundException (
		GIDNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( ResourceNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException (
		ResourceNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( TeamNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleTeamNotFoundException (
		TeamNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( UserNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleUserNotFoundException (
		UserNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( DomainPosterNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleDomainPosterNotFoundException (
		DomainPosterNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( TeamPhotoNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleTeamPhotoNotFoundException (
		TeamPhotoNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( ContactQueryNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleContactQueryNotFoundException (
		ContactQueryNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( ComboNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleComboNotFoundException (
		ComboNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( GalleryNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleGalleryNotFoundException (
		GalleryNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler ( MemberProfileNotFoundException.class )
	public ResponseEntity<ErrorResponse> handleMemberProfileNotFoundException (
		MemberProfileNotFoundException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}


}
