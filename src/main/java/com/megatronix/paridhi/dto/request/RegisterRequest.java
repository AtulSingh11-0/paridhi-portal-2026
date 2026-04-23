package com.megatronix.paridhi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for user registration")
public record RegisterRequest(
	@NotBlank(message = "Name cannot be empty")
	@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
	@Schema(description = "User's full name", example = "John Doe")
	String name,

	@NotBlank(message = "Email cannot be empty")
	@Email(message = "Email should be valid")
	@Schema(description = "User's email address", example = "john.doe@example.com")
	String email,

	@NotBlank(message = "Password cannot be empty")
	@Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
	@Schema(description = "User's chosen password", example = "securepassword123")
	String password
) {}