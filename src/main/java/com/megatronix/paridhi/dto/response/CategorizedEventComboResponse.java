package com.megatronix.paridhi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response payload containing event combos for a specific domain")
public record CategorizedEventComboResponse(
        @Schema(description = "The domain of the event combos", example = "CODING")
        String domain,
        @Schema(description = "List of event combos belonging to the domain")
        List<EventComboPublicResponse> eventCombos
) {}
