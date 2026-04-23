package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.request.OtpRequest;
import com.megatronix.paridhi.dto.request.OtpVerificationRequest;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.MessageResponse;
import com.megatronix.paridhi.dto.response.OtpVerificationResponse;
import com.megatronix.paridhi.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping ( "/api/auth/otp" )
@Tag(name = "OTP", description = "Endpoints for OTP generation, verification, and resending for email verification")
public class OtpController {
	private final OtpService otpService;

	public OtpController ( OtpService otpService ) {
		this.otpService = otpService;
	}

	@PostMapping ( "/send" )
	@Operation(
		summary = "Send OTP",
		description = "Generates a 6-digit OTP and sends it to the user's registered email. " +
			"Has a 60-second cooldown between requests. Any existing active OTPs are invalidated before sending a new one. " +
			"Only works for users who are not yet verified."
	)
	@ApiResponse(responseCode = "200", description = "OTP sent successfully",
		content = @Content(schema = @Schema(implementation = MessageResponse.class),
			examples = @ExampleObject(value = """
				{
				  "message": "OTP sent successfully"
				}""")))
	@ApiResponse(responseCode = "400", description = "Validation error or cooldown period active",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = {
				@ExampleObject(name = "Validation Error", value = """
					{
					  "status": 400,
					  "message": "Validation error",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/otp/send",
					  "validationErrors": [
					    {"field": "email", "message": "Email cannot be empty"}
					  ]
					}"""),
				@ExampleObject(name = "Cooldown Active", value = """
					{
					  "status": 400,
					  "message": "Please wait before requesting a new token. Try again in 45 seconds.",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/otp/send"
					}""")
			}))
	@ApiResponse(responseCode = "404", description = "User not found with the provided email",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 404,
				  "message": "User not found",
				  "error": "Not Found",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/otp/send"
				}""")))
	@ApiResponse(responseCode = "409", description = "User is already verified",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 409,
				  "message": "User is already verified",
				  "error": "Conflict",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/otp/send"
				}""")))
	public ResponseEntity< MessageResponse > sendOtp (
		@Valid @RequestBody OtpRequest request
	) {
		return ResponseEntity.ok(otpService.generateAndSendOtp(request));
	}

	@PostMapping ( "/verify" )
	@Operation(
		summary = "Verify OTP",
		description = "Verifies the provided 6-digit OTP for a user. On success, marks the user as verified. " +
			"Each OTP allows up to 5 attempts before being locked. A locked OTP requires requesting a new one."
	)
	@ApiResponse(responseCode = "200", description = "OTP verified successfully — user is now verified",
		content = @Content(schema = @Schema(implementation = OtpVerificationResponse.class),
			examples = @ExampleObject(value = """
				{
				  "success": true,
				  "message": "OTP verified successfully"
				}""")))
	@ApiResponse(responseCode = "400", description = "Invalid, expired, or locked OTP",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = {
				@ExampleObject(name = "Wrong OTP", value = """
					{
					  "status": 400,
					  "message": "Invalid OTP. Attempts left: 4",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/otp/verify"
					}"""),
				@ExampleObject(name = "Expired OTP", value = """
					{
					  "status": 400,
					  "message": "This token has expired. Please request a new one.",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/otp/verify"
					}"""),
				@ExampleObject(name = "Locked OTP (too many attempts)", value = """
					{
					  "status": 400,
					  "message": "This OTP is locked due to too many failed attempts. Please request a new one.",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/otp/verify"
					}"""),
				@ExampleObject(name = "Invalid Format", value = """
					{
					  "status": 400,
					  "message": "Validation error",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/otp/verify",
					  "validationErrors": [
					    {"field": "otp", "message": "OTP must be 6 digits"}
					  ]
					}""")
			}))
	@ApiResponse(responseCode = "404", description = "User not found with the provided email",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 404,
				  "message": "User not found",
				  "error": "Not Found",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/otp/verify"
				}""")))
	@ApiResponse(responseCode = "409", description = "User is already verified",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 409,
				  "message": "User is already verified",
				  "error": "Conflict",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/otp/verify"
				}""")))
	public ResponseEntity< OtpVerificationResponse > verifyOtp (
		@Valid @RequestBody OtpVerificationRequest request
	) {
		return ResponseEntity.ok(otpService.verifyOtp(request));
	}

	@PostMapping ( "/resend" )
	@Operation(
		summary = "Resend OTP",
		description = "Invalidates any existing active OTP and generates a new one. " +
			"Has a 60-second cooldown between requests. Only works for users who are not yet verified."
	)
	@ApiResponse(responseCode = "200", description = "OTP resent successfully",
		content = @Content(schema = @Schema(implementation = MessageResponse.class),
			examples = @ExampleObject(value = """
				{
				  "message": "OTP resent successfully"
				}""")))
	@ApiResponse(responseCode = "400", description = "Cooldown period active or validation error",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(name = "Cooldown Active", value = """
				{
				  "status": 400,
				  "message": "Please wait before requesting a new token. Try again in 45 seconds.",
				  "error": "Bad Request",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/otp/resend"
				}""")))
	@ApiResponse(responseCode = "404", description = "User not found with the provided email",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 404,
				  "message": "User not found",
				  "error": "Not Found",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/otp/resend"
				}""")))
	@ApiResponse(responseCode = "409", description = "User is already verified",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 409,
				  "message": "User is already verified",
				  "error": "Conflict",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/otp/resend"
				}""")))
	public ResponseEntity< MessageResponse > resendOtp (
		@Valid @RequestBody OtpRequest request
	) {
		return ResponseEntity.ok(otpService.resendOtp(request));
	}
}