package com.megatronix.paridhi.dto.request;

import com.megatronix.paridhi.constant.Domain;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Schema ( description = "Request payload for creating or updating an event combo" )
public record EventComboRequest(

	@NotBlank ( message = "Name cannot be empty" )
	@Schema ( description = "Name of the combo", example = "Mega Coding Pack" )
	String name,

	@NotBlank ( message = "Combo description cannot be null or empty" )
	@Schema ( description = "Detailed description of the combo", example = "A package containing all major coding events." )
	String description,

	@NotNull ( message = "Domain cannot be null" )
	@Schema ( description = "Domain of the combo", example = "CODING" )
	Domain domain,

	@NotEmpty ( message = "Event IDs cannot be empty" )
	@Size ( min = 2, message = "A combo must contain at least 2 events" )
	@Schema ( description = "List of IDs of events to be included in the combo", example = "[1, 2]" )
	List< Long > eventIds,

	@NotNull ( message = "Registration fee cannot be null" )
	@Min ( value = 0, message = "Registration fee cannot be negative" )
	@Schema ( description = "Discounted fee for the combo registration", example = "250.00" )
	BigDecimal registrationFee
) {
}
