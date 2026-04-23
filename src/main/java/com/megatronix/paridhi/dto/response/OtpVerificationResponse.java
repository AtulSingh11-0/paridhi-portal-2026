package com.megatronix.paridhi.dto.response;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "Response payload for OTP verification")
public record OtpVerificationResponse(
	@Schema(description = "Indicates whether the verification was successful", example = "true")
	boolean success,
	@Schema(description = "Verification message", example = "OTP verified successfully")
	String message
) {
	public static OtpVerificationResponse valid ( String message ) {
		return OtpVerificationResponse.builder()
			.success(true)
			.message(message)
			.build();
	}

	public static OtpVerificationResponse invalid ( String message ) {
		return OtpVerificationResponse.builder()
			.success(false)
			.message(message)
			.build();
	}
}
