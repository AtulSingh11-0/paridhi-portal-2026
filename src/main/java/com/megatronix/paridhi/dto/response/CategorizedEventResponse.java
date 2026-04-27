package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Builder
@Schema(description = "Response payload containing events grouped by their domains")
public record CategorizedEventResponse(
	@JsonValue
	@Schema(description = "Map of domains to lists of events in that domain")
	Map< Domain, List< EventPublicResponse > > categorizedEvents
) {
	public static CategorizedEventResponse of ( List< Event > events ) {
		return CategorizedEventResponse.builder()
			.categorizedEvents(events.stream()
				.collect(groupingBy(
					Event::getDomain,
					TreeMap::new,
					Collectors.mapping(EventPublicResponse::fromEvent, Collectors.toList())
				))
			).build();
	}
}
