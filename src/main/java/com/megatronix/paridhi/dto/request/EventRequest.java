package com.megatronix.paridhi.dto.request;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Schema(description = "Request payload for creating or updating an event")
public record EventRequest(
	@NotNull ( message = "Domain cannot be null" )
	@Schema(description = "Domain of the event", example = "CODING")
	Domain domain,

	@NotBlank ( message = "Event name cannot be empty" )
	@Size ( min = 2, max = 100, message = "Event name must be between 2 and 100 characters" )
	@Schema(description = "Name of the event", example = "Code-O-Fiesta")
	String name,

	@NotNull ( message = "Event type cannot be null" )
	@Schema(description = "Type of the event", example = "MAIN")
	EventType type,

	@NotNull ( message = "Event date cannot be null" )
	@Schema(description = "Date and time of the event", example = "1778839200.000000000")
	Instant date,

	@NotBlank ( message = "Description cannot be empty" )
	@Size ( min = 10, message = "Description must be at least 10 characters" )
	@Schema(description = "Detailed description of the event", example = "A high-stakes coding competition for university students.")
	String description,

	@NotBlank ( message = "Venue cannot be empty" )
	@Schema(description = "Location where the event will take place", example = "Lab 1, CS Department")
	String venue,

	@NotNull ( message = "Coordinator details cannot be empty" )
	@Schema(description = "List of coordinator names and contact numbers", example = "[\"John Doe: 9876543210\", \"Jane Smith: 9123456780\"]")
	List< String > coordinatorDetails,

	@Schema(description = "URL to the event's rulebook PDF", example = "https://example.com/rulebook.pdf")
	String ruleBookUrl,

	@NotNull ( message = "Is team event cannot be null" )
	@Schema(description = "Whether the event is a team-based competition", example = "true")
	Boolean isTeamEvent,

	@NotNull ( message = "Min players cannot be null" )
	@Min ( value = 1, message = "Min players must be at least 1" )
	@Schema(description = "Minimum number of players per team", example = "1")
	Integer minPlayers,

	@NotNull ( message = "Max players cannot be null" )
	@Min ( value = 1, message = "Max players must be at least 1" )
	@Schema(description = "Maximum number of players per team", example = "4")
	Integer maxPlayers,

	@NotNull ( message = "Registration fee cannot be null" )
	@Min ( value = 0, message = "Registration fee must be non-negative" )
	@Schema(description = "Fee required to register for the event", example = "150.00")
	BigDecimal registrationFee
) {

}
