package com.megatronix.paridhi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
@Schema ( description = "Request payload for registering a team for an event" )
public record TeamRequest(

	@NotBlank ( message = "Team name cannot be empty" )
	@Size ( min = 2, max = 100, message = "Team name must be between 2 and 100 characters" )
	@Schema ( description = "Name of the team", example = "The Hackers" )
	String teamName,

	@NotNull ( message = "Event ID cannot be null" )
	@Schema ( description = "ID of the event to register for", example = "1" )
	Long eventId,

	@NotEmpty ( message = "GID list cannot be empty" )
	@Schema (
		description = "List of GIDs of the team members",
		example = """
			["PD26-0001-AD", "PD26-0002-AD"]
			"""
	)
	List< String > gidList,

	@Valid
	@NotEmpty ( message = "At least one phoneNumber is required" )
	@Schema ( description = "List of contact persons for the team" )
	List< TeamContactRequest > contacts
) {
}
