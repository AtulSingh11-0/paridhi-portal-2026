package com.megatronix.paridhi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema ( description = "Request payload for adding a contact person to a team" )
public record TeamContactRequest(

	@NotBlank ( message = "Contact name cannot be empty" )
	@Size ( min = 2, max = 100, message = "Contact name must be between 2 and 100 characters" )
	@Schema ( description = "Name of the contact person", example = "John Doe" )
	String name,

	@NotBlank ( message = "Contact number cannot be empty" )
	@Pattern ( regexp = "^\\d{10}$", message = "Contact must be 10 digits" )
	@Schema ( description = "10-digit phone number of the contact person", example = "9876543210" )
	String phoneNumber
) {
}
