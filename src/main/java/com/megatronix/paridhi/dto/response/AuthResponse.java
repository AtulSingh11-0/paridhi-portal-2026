package com.megatronix.paridhi.dto.response;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "Response payload containing authentication token and user details")
public record AuthResponse(
	@Schema(description = "JWT authentication token")
	String token,
	@Schema(description = "Authenticated user details")
	UserResponse user
) {
}