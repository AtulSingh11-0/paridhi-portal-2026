package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude ( NON_NULL )
@Schema (
	name = "ErrorResponse",
	description = "Standard error envelope returned for all failed API responses"
)
public record ErrorResponse(

	@Schema (
		description = "HTTP status code",
		example = "400"
	)
	int status,

	@Schema (
		description = "Human-readable error message",
		example = "Password cannot be empty"
	)
	String message,

	@Schema (
		description = "HTTP status reason phrase",
		example = "Bad Request"
	)
	String error,

	@Schema (
		description = "Unix epoch timestamp (seconds) of when the error occurred",
		example = "1777064620.7507265"
	)
	Instant timestamp,

	@Schema (
		description = "Request path that triggered the error",
		example = "/api/auth/register"
	)
	String path,

	@JsonInclude ( NON_EMPTY )
	@Schema (
		description = "List of field-level validation errors. Only present on 400 Validation Error responses.",
		nullable = true
	)
	List< ValidationError > validationErrors

) {
	public ErrorResponse {
		validationErrors = validationErrors == null ? List.of() : List.copyOf(validationErrors);
	}

	@Builder
	@Schema (
		name = "ValidationError",
		description = "A single field-level validation failure"
	)
	public record ValidationError(

		@Schema (
			description = "The name of the field that failed validation",
			example = "password"
		)
		String field,

		@Schema (
			description = "The validation failure message for this field",
			example = "Password cannot be empty"
		)
		String message

	) {
		public static ValidationError of ( String field, String message ) {
			return new ValidationError(field, message);
		}
	}

	public static ErrorResponse of ( int status, String message, String error, String path ) {
		return ErrorResponse.builder()
			.status(status)
			.message(message)
			.error(error)
			.timestamp(Instant.now())
			.path(path)
			.validationErrors(List.of())
			.build();
	}

	public ErrorResponse withValidationError ( String field, String message ) {
		List< ValidationError > updatedErrors = new ArrayList<>(this.validationErrors);
		updatedErrors.add(new ValidationError(field, message));

		return new ErrorResponse(
			status,
			message,
			error,
			timestamp,
			path,
			updatedErrors
		);
	}
}
