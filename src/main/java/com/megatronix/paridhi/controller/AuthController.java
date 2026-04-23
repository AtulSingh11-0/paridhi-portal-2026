package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.request.LoginRequest;
import com.megatronix.paridhi.dto.request.RegisterRequest;
import com.megatronix.paridhi.dto.response.AuthResponse;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.MessageResponse;
import com.megatronix.paridhi.dto.response.TokenValidationResponse;
import com.megatronix.paridhi.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping ( "/api/auth" )
@Tag(name = "Authentication", description = "Endpoints for user registration, login, token validation, and logout")
public class AuthController {
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	private final UserService userService;

	public AuthController ( UserService userService ) {
		this.userService = userService;
	}

	@PostMapping ( "/register" )
	@Operation(
		summary = "Register a new user",
		description = "Registers a new user account with name, email, and password. Returns a JWT token and user details on success. " +
			"The user is assigned ROLE_USER by default and is initially unverified."
	)
	@ApiResponse(responseCode = "201", description = "User successfully registered",
		content = @Content(schema = @Schema(implementation = AuthResponse.class),
			examples = @ExampleObject(value = """
				{
				  "token": "eyJhbGciOiJIUzI1NiJ9...",
				  "user": {
				    "id": 1,
				    "name": "John Doe",
				    "email": "john.doe@example.com",
				    "isVerified": false,
				    "isProfileCreated": false
				  }
				}""")))
	@ApiResponse(responseCode = "400", description = "Validation error — missing or invalid fields",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 400,
				  "message": "Validation error",
				  "error": "Bad Request",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/register",
				  "validationErrors": [
				    {"field": "name", "message": "Name cannot be empty"},
				    {"field": "email", "message": "Email should be valid"},
				    {"field": "password", "message": "Password must be between 8 and 64 characters"}
				  ]
				}""")))
	@ApiResponse(responseCode = "409", description = "User with this email already exists",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 409,
				  "message": "User already exists",
				  "error": "Conflict",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/register"
				}""")))
	public ResponseEntity< AuthResponse > register ( @Valid @RequestBody RegisterRequest request ) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
	}

	@PostMapping ( "/login" )
	@Operation(
		summary = "Login user",
		description = "Authenticates a user with email and password. Returns a JWT token and full user profile on success. " +
			"Updates the user's last login timestamp."
	)
	@ApiResponse(responseCode = "200", description = "User successfully authenticated",
		content = @Content(schema = @Schema(implementation = AuthResponse.class),
			examples = @ExampleObject(value = """
				{
				  "token": "eyJhbGciOiJIUzI1NiJ9...",
				  "user": {
				    "id": 1,
				    "name": "John Doe",
				    "email": "john.doe@example.com",
				    "isVerified": true,
				    "isProfileCreated": true,
				    "lastLogin": "2026-04-23T01:00:00Z"
				  }
				}""")))
	@ApiResponse(responseCode = "400", description = "Validation error — missing or invalid fields",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 400,
				  "message": "Validation error",
				  "error": "Bad Request",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/login",
				  "validationErrors": [
				    {"field": "email", "message": "Email cannot be empty"}
				  ]
				}""")))
	@ApiResponse(responseCode = "401", description = "Invalid email or password",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 401,
				  "message": "Invalid username or password: Bad credentials",
				  "error": "Unauthorized",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/login"
				}""")))
	@ApiResponse(responseCode = "404", description = "User not found with the provided email",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 404,
				  "message": "User not found",
				  "error": "Not Found",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/login"
				}""")))
	public ResponseEntity< AuthResponse > login ( @Valid @RequestBody LoginRequest request ) {
		return ResponseEntity.ok(userService.login(request));
	}

	@PostMapping ( "/logout" )
	@Operation(
		summary = "Logout user",
		description = "Logs out a user by blacklisting their JWT token. The token is added to a blacklist and will " +
			"be rejected on subsequent requests. Requires a valid Bearer token in the Authorization header."
	)
	@ApiResponse(responseCode = "200", description = "User successfully logged out",
		content = @Content(schema = @Schema(implementation = MessageResponse.class),
			examples = @ExampleObject(value = """
				{
				  "message": "Logout successful"
				}""")))
	@ApiResponse(responseCode = "401", description = "Missing, invalid, or expired JWT token",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class),
			examples = @ExampleObject(value = """
				{
				  "status": 401,
				  "message": "Invalid token",
				  "error": "Unauthorized",
				  "timestamp": "23-04-2026 01:00:00",
				  "path": "/api/auth/logout"
				}""")))
	public ResponseEntity< MessageResponse > logout ( @RequestHeader ( name = "Authorization" ) String authHeader ) {
		userService.logout(authHeader);
		return ResponseEntity.ok(new MessageResponse("Logout successful"));
	}

	/**
	 * Checks if the provided JWT is valid and returns the associated user data.
	 * This is useful for frontends to verify a session on page refresh.
	 */
	@GetMapping ( "/check-token" )
	@Operation(
		summary = "Check token validity",
		description = "Verifies if the provided JWT token is valid, not expired, and not blacklisted. " +
			"Returns the associated user profile if valid. Useful for frontend session checks on page refresh."
	)
	@ApiResponse(responseCode = "200", description = "Token is valid — returns user profile",
		content = @Content(schema = @Schema(implementation = TokenValidationResponse.class),
			examples = @ExampleObject(value = """
				{
				  "isValid": true,
				  "message": "Token is valid",
				  "user": {
				    "id": 1,
				    "name": "John Doe",
				    "email": "john.doe@example.com",
				    "isVerified": true,
				    "isProfileCreated": true
				  }
				}""")))
	@ApiResponse(responseCode = "401", description = "Token is invalid, expired, or has been revoked",
		content = @Content(schema = @Schema(implementation = TokenValidationResponse.class),
			examples = {
				@ExampleObject(name = "Expired Token", value = """
					{
					  "isValid": false,
					  "message": "Token is invalid or expired"
					}"""),
				@ExampleObject(name = "Revoked Token", value = """
					{
					  "isValid": false,
					  "message": "Token has been revoked"
					}"""),
				@ExampleObject(name = "Invalid Header", value = """
					{
					  "isValid": false,
					  "message": "Invalid authorization header"
					}""")
			}))
	public ResponseEntity< TokenValidationResponse > checkTokenValidity ( @RequestHeader ( name = "Authorization" ) String authHeader ) {
		log.info("Checking token validity");

		// Use the new service method to handle the complex validation logic
		TokenValidationResponse response = userService.validateCurrentToken(authHeader);

		if ( response.isValid() ) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
	}
}