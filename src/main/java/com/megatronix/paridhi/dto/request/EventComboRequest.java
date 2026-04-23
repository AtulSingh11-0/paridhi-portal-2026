package com.megatronix.paridhi.dto.request;

import com.megatronix.paridhi.constant.Domain;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record EventComboRequest(
	@NotBlank ( message = "Name cannot be empty" )
	String name,

	@NotBlank ( message = "Combo description cannot be null or empty" )
	String description,

	@NotNull ( message = "Domain cannot be null" )
	Domain domain,

	@NotEmpty ( message = "Event IDs cannot be empty" )
	@Size ( min = 2, message = "A combo must contain at least 2 events" )
	List< Long > eventIds,

	@NotNull ( message = "Registration fee cannot be null" )
	@Min ( value = 0, message = "Registration fee cannot be negative" )
	BigDecimal registrationFee
) {
}
