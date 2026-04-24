package com.megatronix.paridhi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema ( description = "Response payload containing authentication token and user details")
public record AuthResponse(
	@Schema ( description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30" )
	String token,
	/*@Schema(description = "Authenticated user details")
	UserResponse user*/
	@Schema ( description = "Message indicating the result of the authentication operation", example = "Registration successfully" )
	String message
) {
}