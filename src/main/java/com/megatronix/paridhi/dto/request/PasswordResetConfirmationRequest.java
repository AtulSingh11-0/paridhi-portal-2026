package com.megatronix.paridhi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "Request payload for confirming a password reset")
public record PasswordResetConfirmationRequest(
	@NotBlank ( message = "Email cannot be empty" )
	@Email ( message = "Email should be valid" )
	@Schema(description = "User's email address", example = "john.doe@example.com")
	String email,
	@NotBlank ( message = "Token cannot be empty" )
	@Schema(description = "Password reset token sent via email", example = "abc123xyz")
	String token,
	@NotBlank ( message = "Password cannot be empty" )
	@Size ( min = 8, max = 64, message = "Password must be between 8 and 64 characters" )
	@Schema(description = "New password to set", example = "newsecurepassword123")
	String newPassword
) {
}
