package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.request.PasswordResetConfirmationRequest;
import com.megatronix.paridhi.dto.request.PasswordResetRequest;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.MessageResponse;
import com.megatronix.paridhi.dto.response.TokenValidationResponse;
import com.megatronix.paridhi.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ( "/api/auth/password" )
@Tag ( name = "Password Reset", description = "Endpoints for requesting, validating, and confirming password resets" )
public class PasswordResetController {

	private final PasswordResetService passwordResetService;

	public PasswordResetController ( PasswordResetService passwordResetService ) {
		this.passwordResetService = passwordResetService;
	}

	@PostMapping ( "/reset-request" )
	@Operation (
		summary = "Request password reset",
		description = """
			Initiates a password reset by generating a reset token and sending it to the user's email.
			Returns a generic success message regardless of whether the email exists (to prevent email enumeration).
			Has a 60-second cooldown between requests. Any existing active reset tokens are invalidated."""
	)
	@ApiResponse ( responseCode = "200", description = "Password reset request processed (generic message for security)",
		content = @Content ( schema = @Schema ( implementation = MessageResponse.class ),
			examples = @ExampleObject ( value = """
				{
				  "message": "If that email address is registered, an OTP has been sent to it."
				}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation error or cooldown period active",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = {
				@ExampleObject ( name = "Validation Error", value = """
					{
					   "status": 400,
					   "message": "Email should be valid",
					   "error": "Bad Request",
					   "timestamp": 1777135926.3774312,
					   "path": "/api/auth/password/reset-request",
					   "validationErrors": [
					     {
					       "field": "email",
					       "message": "Email should be valid"
					     }
					   ]
					 }""" ),
				@ExampleObject ( name = "Invalid Request Body", value = """
					{
					  "status": 400,
					  "message": "Invalid request body. Please check your input and try again.",
					  "error": "Bad Request",
					  "timestamp": 1777136449.6035898,
					  "path": "/api/auth/password/reset-request"
					}""" )
			} ) )
	@ApiResponse ( responseCode = "429", description = "Too many requests (cooldown active)",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = @ExampleObject ( value = """
				{
				  "status": 429,
				  "message": "Please wait 30 minutes before requesting a new token.",
				  "error": "Too Many Requests",
				  "timestamp": 1777136488.3250964,
				  "path": "/api/auth/password/reset-request"
				}""" ) ) )
	public ResponseEntity< MessageResponse > requestPasswordReset (
		@Valid @RequestBody PasswordResetRequest request
	) {
		return ResponseEntity.ok(passwordResetService.requestPasswordReset(request));
	}

	@PostMapping ( "/reset-confirmation" )
	@Operation (
		summary = "Confirm password reset",
		description = """
			Confirms the password reset using the email, reset token, and a new password.
			The token allows up to 5 attempts before being locked.
			The new password cannot be the same as the old password."""
	)
	@ApiResponse ( responseCode = "200", description = "Password reset successfully",
		content = @Content ( schema = @Schema ( implementation = MessageResponse.class ),
			examples = @ExampleObject ( value = """
				{
				  "message": "Password reset successful"
				}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Invalid/expired token, locked token, same password, or validation error",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = {
				@ExampleObject ( name = "Invalid Request Body", value = """
					{
					  "status": 400,
					  "message": "Invalid request body. Please check your input and try again.",
					  "error": "Bad Request",
					  "timestamp": 1777136449.6035898,
					  "path": "/api/auth/password/reset-confirmation"
					}""" ),
				@ExampleObject ( name = "Validation Error", value = """
					{
					  "status": 400,
					  "message": "Password must be between 8 and 64 characters",
					  "error": "Bad Request",
					  "timestamp": 1777139113.4716053,
					  "path": "/api/auth/password/reset-confirmation",
					  "validationErrors": [
					    {
					      "field": "newPassword",
					      "message": "Password must be between 8 and 64 characters"
					    }
					  ]
					}""" ),
				@ExampleObject ( name = "Invalid Token", value = """
					{
					  "status": 400,
					  "message": "Invalid OTP. Attempts left: 4",
					  "error": "Bad Request",
					  "timestamp": 1777138953.4926937,
					  "path": "/api/auth/password/reset-confirmation"
					}""" ),
				@ExampleObject ( name = "Expired Token", value = """
					{
					  "status": 400,
					  "message": "This token has expired. Please request a new one.",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/reset-confirmation"
					}""" ),
				@ExampleObject ( name = "Locked Token (too many attempts)", value = """
					{
					  "status": 400,
					  "message": "This OTP is locked due to too many failed attempts. Please request a new one.",
					  "error": "Bad Request",
					  "timestamp": 1777138953.4926937,
					  "path": "/api/auth/password/reset-confirmation"
					}""" ),
				@ExampleObject ( name = "Same Password", value = """
					{
					  "status": 500,
					  "message": "New password cannot be same as old password",
					  "error": "Internal Server Error",
					  "timestamp": 1777138953.4926937,
					  "path": "/api/auth/password/reset-confirmation"
					}""" ),
				@ExampleObject ( name = "Token not found or etc.", value = """
					{
					  "status": 400,
					  "message": "Password reset token is INVALID or EXPIRED.",
					  "error": "Bad Request",
					  "timestamp": 1777138953.4926937,
					  "path": "/api/auth/password/reset-confirmation"
					}""" ),
				@ExampleObject ( name = "Invalid Request Details", value = """
					{
					  "status": 400,
					  "message": "Invalid request details",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/reset-confirmation"
					}""" )
			} ) )
	public ResponseEntity< MessageResponse > confirmPasswordReset (
		@Valid @RequestBody PasswordResetConfirmationRequest request
	) {
		return ResponseEntity.ok(passwordResetService.confirmPasswordReset(request));
	}

	@PostMapping ( "/validate-token" )
	@Operation (
		summary = "Validate reset token",
		description = """
			Validates whether a password reset token is still valid (not expired, not used, and not locked).
			This can be used by the frontend to check token validity before showing the reset form."""
	)
	@ApiResponse ( responseCode = "200", description = "Token validation result",
		content = @Content ( schema = @Schema ( implementation = TokenValidationResponse.class ),
			examples = {
				@ExampleObject ( name = "Valid Token", value = """
					{
					  "isValid": true,
					  "message": "Token is valid"
					}""" )
			} ) )
	@ApiResponse ( responseCode = "400", description = "Missing required token parameter, or token is invalid/expired/locked",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = {
				@ExampleObject ( name = "Missing 'token' parameter", value = """
					{
					  "status": 400,
					  "message": "Required parameter 'token' is missing",
					  "error": "Bad Request",
					  "timestamp": "23-04-2026 01:00:00",
					  "path": "/api/auth/password/validate-token"
					}""" ),
				@ExampleObject ( name = "Invalid/Expired/Locked Token", value = """
					{
					  "status": 400,
					  "message": "Password reset token is INVALID or EXPIRED.",
					  "error": "Bad Request",
					  "timestamp": 1777138953.4926937,
					  "path": "/api/auth/password/validate-token"
					}""" )
			} ) )
	public ResponseEntity< TokenValidationResponse > validateResetToken (
		@Valid @RequestParam String token
	) {
		return ResponseEntity.ok(passwordResetService.validateResetToken(token));
	}
}
