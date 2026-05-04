package com.megatronix.paridhi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema ( description = "Request payload for initiating a password reset" )
public record PasswordResetRequest(

	@NotBlank ( message = "Email cannot be empty" )
	@Email ( message = "Email should be valid" )
	@Schema ( description = "User's email address", example = "john.doe@example.com" )
	String email
) {
}
