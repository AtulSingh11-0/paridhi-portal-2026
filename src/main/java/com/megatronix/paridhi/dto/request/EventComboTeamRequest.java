package com.megatronix.paridhi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record EventComboTeamRequest(
	@NotNull ( message = "Combo ID cannot be null" )
	Long comboId,

	@NotBlank ( message = "Team name cannot be empty" )
	@Size ( min = 2, max = 100, message = "Team name must be between 2 and 100 characters" )
	String teamName,

	@NotEmpty ( message = "Event GID mappings cannot be empty" )
	Map< Long, List< String > > eventGidMap,

	@NotEmpty ( message = "At least one phoneNumber is required" )
	@Valid
	List< TeamContactRequest > contacts
	) {
}
