package com.megatronix.paridhi.exception;

import com.megatronix.paridhi.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.megatronix.paridhi.dto.response.ErrorResponse.of;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order ( 4 )
@RestControllerAdvice
public class BadRequestExceptionHandler {

	@ExceptionHandler ( InvalidEmailException.class )
	public ResponseEntity< ErrorResponse > handleInvalidEmailException (
		InvalidEmailException ex,
		HttpServletRequest request
	) {
		ErrorResponse response = of(BAD_REQUEST.value(), ex.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(response);
	}

	@ExceptionHandler ( InvalidDomainException.class )
	public ResponseEntity< ErrorResponse > handleInvalidDomainException (
		InvalidDomainException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( ProfileNotYetCreatedException.class )
	public ResponseEntity< ErrorResponse > handleProfileNotYetCreatedException (
		ProfileNotYetCreatedException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( EmailNotVerifiedException.class )
	public ResponseEntity< ErrorResponse > handleEmailNotVerifiedException (
		EmailNotVerifiedException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( TeamRegistrationException.class )
	public ResponseEntity< ErrorResponse > handleTeamRegistrationException (
		TeamRegistrationException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( InvalidFileException.class )
	public ResponseEntity< ErrorResponse > handleInvalidFileException (
		InvalidFileException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( FileUploadException.class )
	public ResponseEntity< ErrorResponse > handleFileUploadException (
		FileUploadException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( InvalidOtpException.class )
	public ResponseEntity< ErrorResponse > handleInvalidOtpException (
		InvalidOtpException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( InvalidRequestException.class )
	public ResponseEntity< ErrorResponse > handleInvalidRequestException (
		InvalidRequestException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( InvalidResetTokenException.class )
	public ResponseEntity< ErrorResponse > handleInvalidResetTokenException (
		InvalidResetTokenException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( OtpExpiredException.class )
	public ResponseEntity< ErrorResponse > handleOtpExpiredException (
		OtpExpiredException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( TokenExpiredException.class )
	public ResponseEntity< ErrorResponse > handleTokenExpiredException (
		TokenExpiredException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = of(BAD_REQUEST.value(), exception.getMessage(), BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( HttpMessageNotReadableException.class )
	public ResponseEntity< ErrorResponse > handleHttpMessageNotReadableException (
		HttpMessageNotReadableException exception,
		HttpServletRequest request
	) {
		ErrorResponse response = of(BAD_REQUEST.value(), "Invalid request body. Please check your input and try again" +
				".", BAD_REQUEST.getReasonPhrase()
			, request.getRequestURI());
		return ResponseEntity.status(BAD_REQUEST).body(response);
	}
}
