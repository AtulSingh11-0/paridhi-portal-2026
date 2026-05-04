package com.megatronix.paridhi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@Schema ( description = "Request payload for registering a team for an event combo" )
public record EventComboTeamRequest(

	@NotNull ( message = "Combo ID cannot be null" )
	@Schema ( description = "ID of the event combo to register for", example = "1" )
	Long comboId,

	@NotBlank ( message = "Team name cannot be empty" )
	@Size ( min = 2, max = 100, message = "Team name must be between 2 and 100 characters" )
	@Schema ( description = "Name of the team", example = "The Hackers" )
	String teamName,

	@NotEmpty ( message = "Event GID mappings cannot be empty" )
	@Schema (
		description = "Mapping of event IDs to the list of GIDs participating in that event",
		example = """
			{
			  "1": ["PD26-0001-AD", "PD26-0002-AD"],
			  "2": ["PD26-0001-AD"]
			}
			"""
	)
	Map< Long, List< String > > eventGidMap,

	@Valid
	@NotEmpty ( message = "At least one phoneNumber is required" )
	@Schema ( description = "List of contact persons for the team" )
	List< TeamContactRequest > contacts ) {
}
