package com.megatronix.paridhi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static com.megatronix.paridhi.constant.MessageConstant.ToastMessage.OTP_VERIFIED;

@Builder
@Schema ( description = "Response payload for OTP verification" )
public record OtpVerificationResponse(
	@Schema ( description = "Indicates whether the verification was successful", example = "true" )
	boolean success,
	@Schema ( description = "Verification message", example = "OTP verified successfully" )
	String message
) {
	public static OtpVerificationResponse valid () {
		return OtpVerificationResponse.builder()
			.success(true)
			.message(OTP_VERIFIED)
			.build();
	}

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
