package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.megatronix.paridhi.constant.Domain;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record EventComboAdminResponse(
	Long id,
	Domain domain,
	String name,
	String description,
	List< EventAdminResponse > events,
	String posterUrl,
	String posterFileId,
	BigDecimal registrationFee,
	@JsonProperty ( "isRegistrationOpen" )
	boolean isRegistrationOpen,
	Instant createdAt,
	Instant updatedAt,
	String createdByUsername,
	String updatedByUsername
) {
	public static EventComboAdminResponse fromCombo ( com.megatronix.paridhi.model.EventCombo combo ) {
		return EventComboAdminResponse.builder()
			.id(combo.getEventComboId())
			.domain(combo.getDomain())
			.name(combo.getEventComboName())
			.description(combo.getEventComboDescription())
			.events(
				combo.getEvents().stream()
					.map(EventAdminResponse::fromEvent)
					.toList()
			)
			.posterUrl(combo.getEventComboPosterUrl())
			.posterFileId(combo.getEventComboPosterFileId())
			.registrationFee(combo.getEventComboRegistrationFee())
			.isRegistrationOpen(combo.isEventComboRegistrationOpen())
			.createdAt(combo.getCreatedAt())
			.updatedAt(combo.getUpdatedAt())
			.createdByUsername(combo.getCreatedBy())
			.updatedByUsername(combo.getUpdatedBy())
			.build();
	}
}
