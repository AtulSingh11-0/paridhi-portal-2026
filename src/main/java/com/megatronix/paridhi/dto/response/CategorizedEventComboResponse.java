package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.EventCombo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Builder
@Schema(description = "Response payload containing event combos grouped by their domains")
public record CategorizedEventComboResponse(
	@JsonValue
	@Schema(description = "Map of domains to lists of combos in that domain")
	Map< Domain, List< EventComboPublicResponse > > categorizedCombos
) {
	public static CategorizedEventComboResponse of ( List< EventCombo > combos ) {
		return CategorizedEventComboResponse.builder()
			.categorizedCombos(combos.stream()
				.map(EventComboPublicResponse::fromCombo)
				.collect(groupingBy(
					EventComboPublicResponse::domain,
					TreeMap::new,
					Collectors.toList())
				)
			).build();
	}
}
