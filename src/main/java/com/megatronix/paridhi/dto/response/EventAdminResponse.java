package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import com.megatronix.paridhi.model.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Schema(description = "Response payload for detailed event information (Admin view)")
public record EventAdminResponse(
	@Schema(description = "Unique identifier of the event", example = "1")
	Long id,
	@Schema(description = "Domain of the event", example = "CODING")
	Domain domain,
	@Schema(description = "Name of the event", example = "Code-O-Fiesta")
	String name,
	@Schema(description = "Type of the event", example = "MAIN")
	EventType type,
	@Schema(description = "Date and time of the event", example = "1778839200.000000000")
	Instant date,
	@Schema(description = "Detailed description of the event", example = "A high-stakes coding competition.")
	String description,
	@Schema(description = "Location where the event will take place", example = "Lab 1")
	String venue,
	@Schema(description = "List of coordinator details", example = "[\"John Doe - 9876543210\"]")
	List< String > coordinatorDetails,
	@Schema(description = "URL to the event poster image", example = "https://ik.imagekit.io/...")
	String posterUrl,
	@Schema(description = "Internal ID of the poster file in storage", example = "69e0d6...")
	String posterFileId,
	@Schema(description = "URL to the event's rulebook PDF", example = "https://example.com/rulebook.pdf")
	String ruleBookUrl,
	@JsonProperty ( "isTeamEvent" )
	@Schema(description = "Whether the event is a team-based competition", example = "true")
	boolean isTeamEvent,
	@Schema(description = "Minimum number of players per team", example = "1")
	Integer minPlayers,
	@Schema(description = "Maximum number of players per team", example = "4")
	Integer maxPlayers,
	@Schema(description = "Fee required to register for the event", example = "150.00")
	BigDecimal registrationFee,
	@JsonProperty ( "isRegistrationOpen" )
	@Schema(description = "Whether registration is currently open for this event", example = "true")
	boolean isRegistrationOpen,
	@Schema(description = "Timestamp when the event was created", example = "1778839200.000000000")
	Instant createdAt,
	@Schema(description = "Timestamp when the event was last updated", example = "1778839200.000000000")
	Instant updatedAt,
	@Schema(description = "Username of the admin who created the event", example = "john@doe.com")
	String createdByUsername,
	@Schema(description = "Username of the admin who last updated the event", example = "john@doe.com")
	String updatedByUsername
) {
	public static EventAdminResponse fromEvent ( Event event ) {
		return EventAdminResponse.builder()
			.id(event.getEventId())
			.domain(event.getDomain())
			.name(event.getEventName())
			.type(event.getEventType())
			.date(event.getEventDate())
			.description(event.getEventDescription())
			.venue(event.getEventVenue())
			.coordinatorDetails(event.getEventCoordinatorDetails())
			.posterUrl(event.getEventPosterUrl())
			.posterFileId(event.getEventPosterFileId())
			.ruleBookUrl(event.getEventRuleBookUrl())
			.isTeamEvent(event.isTeamEvent())
			.minPlayers(event.getEventMinPlayers())
			.maxPlayers(event.getEventMaxPlayers())
			.registrationFee(event.getEventRegistrationFee())
			.isRegistrationOpen(event.isEventRegistrationOpen())
			.createdAt(event.getCreatedAt())
			.updatedAt(event.getUpdatedAt())
			.createdByUsername(event.getCreatedBy())
			.updatedByUsername(event.getUpdatedBy())
			.build();
	}
}
