package com.megatronix.paridhi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema ( description = "Request payload for verifying an OTP" )
public record OtpVerificationRequest(

	@NotBlank ( message = "Email cannot be empty" )
	@Email ( message = "Email should be valid" )
	@Schema ( description = "User's email address", example = "john.doe@example.com" )
	String email,

	@NotBlank ( message = "OTP cannot be empty" )
	@Pattern ( regexp = "^\\d{6}$", message = "OTP must be 6 digits" )
	@Schema ( description = "6-digit OTP code", example = "123456" )
	String otp
) {

}
