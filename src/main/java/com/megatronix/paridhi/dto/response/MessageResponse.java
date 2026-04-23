package com.megatronix.paridhi.dto.response;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "Generic message response")
public record MessageResponse(
	@Schema(description = "Response message", example = "Operation successful")
	String message
) {
	public static MessageResponse of ( String message ) {
		return MessageResponse.builder()
			.message(message)
			.build();
	}
}
