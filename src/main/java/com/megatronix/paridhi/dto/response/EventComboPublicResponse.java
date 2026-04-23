package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.EventCombo;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record EventComboPublicResponse(
	Long id,
	Domain domain,
	String name,
	String description,
	List< EventPublicResponse > events,
	String posterUrl,
	BigDecimal registrationFee,
	@JsonProperty ( "isRegistrationOpen" )
	boolean isRegistrationOpen
) {
	public static EventComboPublicResponse fromCombo ( EventCombo combo ) {
		return EventComboPublicResponse.builder()
			.id(combo.getEventComboId())
			.name(combo.getEventComboName())
			.description(combo.getEventComboDescription())
			.domain(combo.getDomain())
			.events(
				combo.getEvents().stream()
					.map(EventPublicResponse::fromEvent)
					.toList()
			)
			.posterUrl(combo.getEventComboPosterUrl())
			.registrationFee(combo.getEventComboRegistrationFee())
			.isRegistrationOpen(combo.isEventComboRegistrationOpen())
			.build();
	}
}
