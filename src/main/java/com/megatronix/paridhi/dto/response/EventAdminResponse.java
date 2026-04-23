package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import com.megatronix.paridhi.model.Event;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record EventAdminResponse(
	Long id,
	Domain domain,
	String name,
	EventType type,
	Instant date,
	String description,
	String venue,
	List< String > coordinatorDetails,
	String posterUrl,
	String posterFileId,
	String ruleBookUrl,
	@JsonProperty ( "isTeamEvent" )
	boolean isTeamEvent,
	Integer minPlayers,
	Integer maxPlayers,
	BigDecimal registrationFee,
	@JsonProperty ( "isRegistrationOpen" )
	boolean isRegistrationOpen,
	BigDecimal prizePool,
	Instant createdAt,
	Instant updatedAt,
	String createdByUsername,
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
			.prizePool(event.getEventPrizePool())
			.createdAt(event.getCreatedAt())
			.updatedAt(event.getUpdatedAt())
			.createdByUsername(event.getCreatedBy())
			.updatedByUsername(event.getUpdatedBy())
			.build();
	}
}
