package com.megatronix.paridhi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema ( description = "Response payload containing events for a specific domain" )
public record CategorizedEventResponse(

	@Schema ( description = "The domain of the events", example = "CODING" ) String domain,

	@Schema ( description = "List of events belonging to the domain" ) List< EventPublicResponse > events
) {
}
