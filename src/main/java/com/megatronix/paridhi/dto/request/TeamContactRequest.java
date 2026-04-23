package com.megatronix.paridhi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TeamContactRequest(
	@NotBlank ( message = "Contact name cannot be empty" )
	@Size ( min = 2, max = 100, message = "Contact name must be between 2 and 100 characters" )
	String name,

	@NotBlank ( message = "Contact number cannot be empty" )
	@Pattern ( regexp = "^\\d{10}$", message = "Contact must be 10 digits" )
	String phoneNumber
) {
}
