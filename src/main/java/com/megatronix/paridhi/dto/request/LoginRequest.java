package com.megatronix.paridhi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema ( description = "Request payload for user login" )
public record LoginRequest(

	@NotBlank ( message = "Email cannot be empty" )
	@Email ( message = "Email should be valid" )
	@Schema ( description = "User's email address", example = "john.doe@example.com" )
	String email,

	@NotBlank ( message = "Password cannot be empty" )
	@Schema ( description = "User's password", example = "password123" )
	String password
) {
}