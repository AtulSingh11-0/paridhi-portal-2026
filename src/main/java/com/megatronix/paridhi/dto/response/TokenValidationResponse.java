package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude ( NON_NULL )
@Schema(description = "Response payload indicating token validity")
public record TokenValidationResponse(
	@Schema(description = "Indicates whether the token is valid", example = "true")
	boolean isValid,
	@Schema(description = "Message describing the token status", example = "Token is valid")
	String message,
	@Schema(description = "Authenticated user details if the token is valid")
	UserResponse user
) {
	public static TokenValidationResponse valid () {
		return TokenValidationResponse.builder()
			.isValid(true)
			.message("Token is valid")
			.build();
	}

	public static TokenValidationResponse invalid ( String reason ) {
		return TokenValidationResponse.builder()
			.isValid(false)
			.message(reason)
			.build();
	}
}
