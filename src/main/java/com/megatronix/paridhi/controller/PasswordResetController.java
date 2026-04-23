package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.request.PasswordResetConfirmationRequest;
import com.megatronix.paridhi.dto.request.PasswordResetRequest;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.MessageResponse;
import com.megatronix.paridhi.dto.response.TokenValidationResponse;
import com.megatronix.paridhi.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping ( "/api/auth/password" )
@Tag(name = "Password Reset", description = "Endpoints for requesting, validating, and confirming password resets")
public class PasswordResetController {
	private final PasswordResetService passwordResetService;

	public PasswordResetController ( PasswordResetService passwordResetService ) {
		this.passwordResetService = passwordResetService;
	}

	@PostMapping ( "/reset-request" )
	@Operation(
		summary = "Request password reset",
		description = "Initiates a password reset by generating a reset token and sending it to the user's email. " +
			"Returns a generic success message regardless of whether the email exists (to prevent email enumeration). " +
			"Has a 60-second cooldown between requests. Any existing active reset tokens are invalidated."
	)
	@ApiResponse(responseCode = "200", description = "Password reset request processed (generic message for security)",
		content = @Content(schema = @Schema(implementation = MessageResponse.class),
			examples = @ExampleObject(value = """
				{
				  "message": "If that email address is registered, an OTP has been sent to it."
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
					  "path": "/api/auth/password/reset-request",
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
					  "path": "/api/auth/password/reset-request"
					}""")
			}))
	public ResponseEntity< MessageResponse > requestPasswordReset (
		@Valid @RequestBody PasswordResetRequest request
	) {
		return ResponseEntity.ok(passwordResetService.requestPasswordReset(request));
	}

	@PostMapping ( "/reset-confirmation" )
	@Operation(
		summary = "Confirm password reset",
		description = "Confirms the password reset using the email, reset token, and a new password. " +
			"The token allows up to 5 attempts before being locked. " +
			"The new password cannot be the same as the old password."
	)
	@ApiResponse(responseCode = "200", description = "Password reset successfully",
		content = @Content(schema = @Schema(implementation = MessageResponse.class),
			examples = @ExampleObject(value = """
				{
				  "message": "Password reset successful"
				}""")))
	@ApiResponse(responseCode = "400", description = "Invalid/expired token, locked token, same password, or validation error",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = {
				@ExampleObject(name = "Invalid Token", value = """
					{
					  "status": 400,
					  "message": "Invalid OTP. Attempts left: 4",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/reset-confirmation"
					}"""),
				@ExampleObject(name = "Expired Token", value = """
					{
					  "status": 400,
					  "message": "This token has expired. Please request a new one.",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/reset-confirmation"
					}"""),
				@ExampleObject(name = "Locked Token (too many attempts)", value = """
					{
					  "status": 400,
					  "message": "This OTP is locked due to too many failed attempts. Please request a new one.",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/reset-confirmation"
					}"""),
				@ExampleObject(name = "Same Password", value = """
					{
					  "status": 500,
					  "message": "New password cannot be same as old password",
					  "error": "Internal Server Error",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/reset-confirmation"
					}"""),
				@ExampleObject(name = "No Token Found", value = """
					{
					  "status": 400,
					  "message": "No password reset token found for the provided email",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/reset-confirmation"
					}"""),
				@ExampleObject(name = "Invalid Request Details", value = """
					{
					  "status": 400,
					  "message": "Invalid request details",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/reset-confirmation"
					}""")
			}))
	public ResponseEntity< MessageResponse > confirmPasswordReset (
		@Valid @RequestBody PasswordResetConfirmationRequest request
	) {
		return ResponseEntity.ok(passwordResetService.confirmPasswordReset(request));
	}

	@PostMapping ( "/validate-token" )
	@Operation(
		summary = "Validate reset token",
		description = "Validates whether a password reset token is still valid (not expired, not used, and not locked). " +
			"This can be used by the frontend to check token validity before showing the reset form."
	)
	@ApiResponse(responseCode = "200", description = "Token validation result",
		content = @Content(schema = @Schema(implementation = TokenValidationResponse.class),
			examples = {
				@ExampleObject(name = "Valid Token", value = """
					{
					  "isValid": true,
					  "message": "Token is valid"
					}"""),
				@ExampleObject(name = "Invalid Token", value = """
					{
					  "isValid": false,
					  "message": "Invalid or expired token"
					}""")
			}))
	@ApiResponse(responseCode = "400", description = "Missing required token parameter",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 400,
				  "message": "Required parameter 'token' is missing",
				  "error": "Bad Request",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/password/validate-token"
				}""")))
	public ResponseEntity< TokenValidationResponse > validateResetToken (
		@Valid @RequestParam String token
	) {
		return ResponseEntity.ok(passwordResetService.validateResetToken(token));
	}
}
