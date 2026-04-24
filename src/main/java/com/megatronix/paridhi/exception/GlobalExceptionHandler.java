package com.megatronix.paridhi.exception;

import com.megatronix.paridhi.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Set;

@Slf4j
@Order(5)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/************************************************************** CATCH-ALL / SERVER ERROR **************************************************************/

	@ExceptionHandler ( Exception.class )
	public ResponseEntity<ErrorResponse> handleAllUncaughtException (
		Exception exception,
		HttpServletRequest httpRequest
	) {
		log.error("Unhandled exception at [{}]: {}", httpRequest.getRequestURI(), exception.getMessage(), exception);
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), httpRequest.getRequestURI());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	@ExceptionHandler ( MailSendingException.class )
	public ResponseEntity<ErrorResponse> handleMailSendingException (
		MailSendingException exception,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	/************************************************************** VALIDATION EXCEPTION's **************************************************************/

	@ExceptionHandler ( ConstraintViolationException.class )
	public ResponseEntity<ErrorResponse> handleConstraintViolationException (
		ConstraintViolationException ex,
		HttpServletRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Validation error", HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		for ( var violation : ex.getConstraintViolations() ) {
			errorResponse = errorResponse.withValidationError(
				violation.getPropertyPath().toString(),
				violation.getMessage()
			);
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler ( MethodArgumentTypeMismatchException.class )
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException (
		MethodArgumentTypeMismatchException exception,
		HttpServletRequest request
	) {
		String expectedType = "unknown";
		if ( exception.getRequiredType() != null ) {
			expectedType = exception.getRequiredType().getSimpleName();
		}

		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid parameter type. Expected " +
			expectedType +
			" but got " + exception.getValue(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/************************************************************** SPRING MVC OVERRIDES **************************************************************/

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid (
		@NonNull MethodArgumentNotValidException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status, @NonNull WebRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Validation error", HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getDescription(false).substring(4));

		for ( var fieldError : ex.getBindingResult().getFieldErrors() ) {
			errorResponse = errorResponse.withValidationError(
				fieldError.getField(),
				fieldError.getDefaultMessage()
			);
		}

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable (
		@NonNull org.springframework.http.converter.HttpMessageNotReadableException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Malformed JSON request: " + ex.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getDescription(false).substring(4));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter (
		@NonNull MissingServletRequestParameterException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Required parameter '" + ex.getParameterName() + "' is missing", HttpStatus.BAD_REQUEST.getReasonPhrase(), request.getDescription(false).substring(4));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@Override
	protected ResponseEntity<Object> handleMaxUploadSizeExceededException (
		@NonNull MaxUploadSizeExceededException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.PAYLOAD_TOO_LARGE.value(), "File size exceeds the maximum allowed size", HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(), request.getDescription(false).substring(4));

		return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported (
		@NonNull org.springframework.web.HttpRequestMethodNotSupportedException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request
	) {
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request. Supported methods are ");
		Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
		if ( supportedMethods != null && ! supportedMethods.isEmpty() ) {
			supportedMethods.forEach(method -> builder.append(method).append(" "));
		} else {
			builder.append("none");
		}

		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED.value(), builder.toString(), HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), request.getDescription(false).substring(4));

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported (
		@NonNull org.springframework.web.HttpMediaTypeNotSupportedException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request
	) {
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		ex.getSupportedMediaTypes().forEach(mediaType -> builder.append(mediaType).append(", "));

		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), builder.substring(0, builder.length() - 2), HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), request.getDescription(false).substring(4));

		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException (
		@NonNull NoHandlerFoundException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request
	) {
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()), HttpStatus.NOT_FOUND.getReasonPhrase(), request.getDescription(false).substring(4));

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

}
