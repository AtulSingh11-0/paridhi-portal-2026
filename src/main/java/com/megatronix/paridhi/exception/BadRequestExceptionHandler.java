package com.megatronix.paridhi.exception;

import com.megatronix.paridhi.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(4)
@RestControllerAdvice
public class BadRequestExceptionHandler {

	@ExceptionHandler ( InvalidDomainException.class )
	public ResponseEntity<ErrorResponse> handleInvalidDomainException (
		InvalidDomainException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( ProfileNotYetCreatedException.class )
	public ResponseEntity<ErrorResponse> handleProfileNotYetCreatedException (
		ProfileNotYetCreatedException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( MemberProfileAlreadyExistsException.class )
	public ResponseEntity<ErrorResponse> handleMemberProfileAlreadyExistsException (
		MemberProfileAlreadyExistsException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( EmailNotVerifiedException.class )
	public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException (
		EmailNotVerifiedException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( TeamRegistrationException.class )
	public ResponseEntity<ErrorResponse> handleTeamRegistrationException (
		TeamRegistrationException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( DomainPosterAlreadyExistsExcpetion.class )
	public ResponseEntity<ErrorResponse> handleDomainPosterAlreadyExistsException (
		DomainPosterAlreadyExistsExcpetion exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( InvalidFileException.class )
	public ResponseEntity<ErrorResponse> handleInvalidFileException (
		InvalidFileException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( FileUploadException.class )
	public ResponseEntity<ErrorResponse> handleFileUploadException (
		FileUploadException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( InvalidOtpException.class )
	public ResponseEntity<ErrorResponse> handleInvalidOtpException (
		InvalidOtpException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( InvalidRequestException.class )
	public ResponseEntity<ErrorResponse> handleInvalidRequestException (
		InvalidRequestException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( InvalidResetTokenException.class )
	public ResponseEntity<ErrorResponse> handleInvalidResetTokenException (
		InvalidResetTokenException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( OtpExpiredException.class )
	public ResponseEntity<ErrorResponse> handleOtpExpiredException (
		OtpExpiredException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( TokenExpiredException.class )
	public ResponseEntity<ErrorResponse> handleTokenExpiredException (
		TokenExpiredException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

}
