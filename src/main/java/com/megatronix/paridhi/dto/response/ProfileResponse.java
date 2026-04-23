package com.megatronix.paridhi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response payload wrapping user profile details")
public record ProfileResponse(
	@Schema(description = "Complete user profile details")
	UserResponse profileDetails
) {
}
