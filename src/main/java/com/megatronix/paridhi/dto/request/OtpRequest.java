package com.megatronix.paridhi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for requesting an OTP")
public record OtpRequest(
	@NotBlank ( message = "Email cannot be empty" )
	@Email ( message = "Email should be valid" )
	@Schema(description = "User's email address to send the OTP to", example = "john.doe@example.com")
	String email
) {
}
