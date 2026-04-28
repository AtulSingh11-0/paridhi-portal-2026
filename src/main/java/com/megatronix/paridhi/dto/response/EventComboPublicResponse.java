package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.EventCombo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Schema(description = "Response payload for public event combo information")
public record EventComboPublicResponse(
	@Schema(description = "Unique identifier of the combo", example = "1")
	Long id,
	@Schema(description = "Domain of the combo", example = "CODING")
	Domain domain,
	@Schema(description = "Name of the combo", example = "Mega Coding Pack")
	String name,
	@Schema(description = "Detailed description of the combo", example = "Includes Code-O-Fiesta and ProbleMatrix.")
	String description,
	@Schema(description = "List of events included in this combo")
	List< EventPublicResponse > events,
	@Schema(description = "URL to the combo poster image", example = "https://ik.imagekit.io/...")
	String posterUrl,
	@Schema(description = "Discounted fee for the combo registration", example = "250.00")
	BigDecimal registrationFee,
	@JsonProperty ( "isRegistrationOpen" )
	@Schema(description = "Whether registration is currently open for this combo", example = "true")
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
