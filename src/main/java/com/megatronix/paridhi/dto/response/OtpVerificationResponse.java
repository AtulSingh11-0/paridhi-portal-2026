package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.megatronix.paridhi.constant.MessageConstant.ToastMessage.OTP_VERIFIED;

@Builder
@JsonInclude ( NON_NULL )
@Schema ( description = "Response payload for OTP verification" )
public record OtpVerificationResponse(
	@Schema ( description = "Indicates whether the verification was successful", example = "true" )
	boolean success,
	@Schema ( description = "Verification message", example = "OTP verified successfully" )
	String message,
	@JsonInclude ( NON_EMPTY )
	@Schema ( description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30" )
	String token
) {
	public static OtpVerificationResponse valid ( String token ) {
		return OtpVerificationResponse.builder()
			.success(true)
			.message(OTP_VERIFIED)
			.token(token)
			.build();
	}

	public static OtpVerificationResponse valid ( String message, String token ) {
		return OtpVerificationResponse.builder()
			.success(true)
			.message(message)
			.token(token)
			.build();
	}

	public static OtpVerificationResponse invalid ( String message ) {
		return OtpVerificationResponse.builder()
			.success(false)
			.message(message)
			.build();
	}
}
