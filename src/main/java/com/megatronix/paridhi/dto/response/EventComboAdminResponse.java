package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.EventCombo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Schema ( description = "Response payload for detailed event combo information (Admin view)" )
public record EventComboAdminResponse(
	@Schema ( description = "Unique identifier of the combo", example = "1" )
	Long id,
	@Schema ( description = "Domain of the combo", example = "CODING" )
	Domain domain,
	@Schema ( description = "Name of the combo", example = "Mega Coding Pack" )
	String name,
	@Schema ( description = "Type of the combo", example = "COMBO" )
	String type,
	@Schema ( description = "Date and time when the combo becomes active", example = "1778839200.000000000" )
	Instant date,
	@Schema ( description = "Detailed description of the combo", example = "Includes all coding events." )
	String description,
	@Schema ( description = "Location where the combo events will take place", example = "Main Auditorium" )
	String venue,
	@Schema ( description = "List of events included in this combo (Admin view)" )
	List< EventAdminResponse > events,
	@Schema ( description = "URL to the combo poster image", example = "https://ik.imagekit.io/..." )
	String posterUrl,
	@Schema ( description = "Internal ID of the poster file in storage", example = "69e0d6..." )
	String posterFileId,
	@Schema ( description = "Discounted fee for the combo registration", example = "250.00" )
	BigDecimal registrationFee,
	@JsonProperty ( "isRegistrationOpen" )
	@Schema ( description = "Whether registration is currently open for this combo", example = "true" )
	boolean isRegistrationOpen,
	@Schema ( description = "Timestamp when the combo was created", example = "1778839200.000000000" )
	Instant createdAt,
	@Schema ( description = "Timestamp when the combo was last updated", example = "1778839200.000000000" )
	Instant updatedAt,
	@Schema ( description = "Username of the admin who created the combo", example = "john@doe.com" )
	String createdByUsername,
	@Schema ( description = "Username of the admin who last updated the combo", example = "john@doe.com" )
	String updatedByUsername
) {
	public static EventComboAdminResponse fromCombo ( EventCombo combo, Instant date, String venue ) {
		return EventComboAdminResponse.builder()
			.id(combo.getEventComboId())
			.domain(combo.getDomain())
			.name(combo.getEventComboName())
			.type("COMBO")
			.date(date)
			.description(combo.getEventComboDescription())
			.venue(venue)
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
