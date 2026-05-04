package com.megatronix.paridhi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MRDRequest(

	@NotBlank ( message = "Email cannot be empty" )
	@Email ( message = "Email should be valid" )
	String email
) {
}