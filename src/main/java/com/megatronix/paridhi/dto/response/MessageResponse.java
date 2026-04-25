package com.megatronix.paridhi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema ( description = "Generic message response" )
public record MessageResponse(
	@Schema ( description = "Response message", example = "Operation successful" )
	String message
) {
	public static MessageResponse of ( String message ) {
		return MessageResponse.builder()
			.message(message)
			.build();
	}
}
