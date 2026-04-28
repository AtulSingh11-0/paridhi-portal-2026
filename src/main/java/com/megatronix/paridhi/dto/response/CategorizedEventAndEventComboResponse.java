package com.megatronix.paridhi.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CategorizedEventAndEventComboResponse(
	String domain,
	List< EventPublicResponse > events,
	List< EventComboPublicResponse > eventCombos
) {
}
