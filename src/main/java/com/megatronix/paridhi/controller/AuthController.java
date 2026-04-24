package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.request.LoginRequest;
import com.megatronix.paridhi.dto.request.RegisterRequest;
import com.megatronix.paridhi.dto.response.AuthResponse;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.MessageResponse;
import com.megatronix.paridhi.dto.response.TokenValidationResponse;
import com.megatronix.paridhi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.megatronix.paridhi.constant.MessageConstant.ToastMessage.LOGOUT_SUCCESSFUL;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping ( "/api/auth" )
@Tag ( name = "Authentication", description = "Endpoints for user registration, login, token validation, and logout" )
public class AuthController {

	private final UserService userService;

	public AuthController ( UserService userService ) {
		this.userService = userService;
	}

	@PostMapping ( "/register" )
	@Operation ( summary = "Register a new user", description = "Registers a new user account with name, email, and password. Returns a JWT token on success. " + "The user is assigned ROLE_USER by default and is initially unverified.", requestBody = @RequestBody ( required = true, content = @Content ( schema = @Schema ( implementation = RegisterRequest.class ), examples = {@ExampleObject ( name = "201 – Valid Registration", summary = "All fields valid → 201 Created", value = """
		{
		  "name": "Atul Singh",
		  "email": "cocatul11@gmail.com",
		  "password": "securepassword123"
		}""" ), @ExampleObject ( name = "400 – Missing Password", summary = "Password omitted → 400 Validation Error", value = """
		{
		  "name": "Atul Singh",
		  "email": "cocatul11@gmail.com"
		}""" ), @ExampleObject ( name = "400 – Malformed JSON", summary = "Broken JSON syntax → 400 Bad Request", value = """
		{
		  "name": "Atul Singh",
		  "email": "cocatul11@gmail.com"
		  "password": "securepassword123"
		}""" ), @ExampleObject ( name = "409 – Duplicate Email", summary = "Email already registered → 409 Conflict",
		value = """
		{
		  "name": "Atul Singh",
		  "email": "cocatul11@gmail.com",
		  "password": "securepassword123"
		}""" )} ) ) )
	@ApiResponse ( responseCode = "201", description = "User successfully registered", content = @Content ( schema = @Schema ( implementation = AuthResponse.class ), examples = @ExampleObject ( name = "Success", value = """
		{
		 "token": "eyJhbGciOiJIUzUxMi...",
		  "message": "Registration successful."
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation error or malformed JSON", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = {@ExampleObject ( name = "Validation Error", value = """
		{
		  "status": 400,
		  "message": "Password cannot be empty",
		  "error": "Bad Request",
		  "timestamp": 1777064620.7507265,
		  "path": "/api/auth/register",
		  "validationErrors": [
		    {
		      "field": "password",
		      "message": "Password cannot be empty"
		    }
		  ]
		}""" ), @ExampleObject ( name = "Malformed JSON", value = """
		{
		  "status": 400,
		  "message": "Malformed JSON request: JSON parse error: Unexpected character ('}' (code 125)): was expecting double-quote to start field name",
		  "error": "Bad Request",
		  "timestamp": 1777064593.3380234,
		  "path": "/api/auth/register"
		}""" )} ) )
	@ApiResponse ( responseCode = "409", description = "User with this email already exists", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( name = "User Already Exists", value = """
		{
		  "status": 409,
		  "message": "This email is already registered. Log in instead or Reset your password.",
		  "error": "Conflict",
		  "timestamp": 1777064641.9844992,
		  "path": "/api/auth/register"
		}""" ) ) )
	public ResponseEntity< AuthResponse > register ( @Valid @org.springframework.web.bind.annotation.RequestBody RegisterRequest request ) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
	}

	@PostMapping ( "/login" )
	@Operation ( summary = "Login user", description = "Authenticates a user with email and password. Returns a JWT token on success. " + "Updates the user's last login timestamp.", requestBody = @RequestBody ( required = true, content = @Content ( schema = @Schema ( implementation = LoginRequest.class ), examples = {@ExampleObject ( name = "200 – Valid Credentials", summary = "Correct email and password → 200 OK", value = """
		{
		  "email": "atul@email.com",
		  "password": "securepassword123"
		}""" ), @ExampleObject ( name = "400 – Missing Password", summary = "Password field absent → 400 Validation Error", value = """
		{
		  "email": "atul@email.com"
		}""" ), @ExampleObject ( name = "401 – Wrong Password", summary = "Email exists but password is incorrect → 401 Unauthorized", value = """
		{
		  "email": "atul@email.com",
		  "password": "wrongpassword"
		}""" ), @ExampleObject ( name = "401 – Unknown Email", summary = "Email not registered → 401 Unauthorized", value = """
		{
		  "email": "notregistered@email.com",
		  "password": "anypassword123"
		}""" )} ) ) )
	@ApiResponse ( responseCode = "200", description = "User successfully authenticated", content = @Content ( schema = @Schema ( implementation = AuthResponse.class ), examples = @ExampleObject ( name = "Success", value = """
		{
		  "token": "eyJhbGciOiJIUzUxMi...",
		  "message": "Login successful."
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation error — missing or invalid fields", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( name = "Validation Error", value = """
		{
		  "status": 400,
		  "message": "Password cannot be empty",
		  "error": "Bad Request",
		  "timestamp": 1777064102.7787747,
		  "path": "/api/auth/login",
		  "validationErrors": [
		    {
		      "field": "password",
		      "message": "Password cannot be empty"
		    }
		  ]
		}""" ) ) )
	@ApiResponse ( responseCode = "401", description = "Invalid email or password", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( name = "Bad Credentials", value = """
		{
		  "status": 401,
		  "message": "Invalid username or password: Bad credentials",
		  "error": "Unauthorized",
		  "timestamp": 1777063539.9881623,
		  "path": "/api/auth/login"
		}""" ) ) )
	public ResponseEntity< AuthResponse > login ( @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request ) {
		return ResponseEntity.ok(userService.login(request));
	}

	@PostMapping ( "/logout" )
	@Operation ( summary = "Logout user", description = "Logs out a user by blacklisting their JWT token. The token is added to a blacklist and will " + "be rejected on subsequent requests. Requires a valid Bearer token in the Authorization header." )
	@ApiResponse ( responseCode = "200", description = "User successfully logged out", content = @Content ( schema = @Schema ( implementation = MessageResponse.class ), examples = @ExampleObject ( name = "Success", value = """
		{
		  "message": "Logout successful."
		}""" ) ) )
	@ApiResponse ( responseCode = "401", description = "Missing, invalid, or expired JWT token", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( name = "Invalid Token", value = """
		{
		  "status": 401,
		  "message": "Invalid token",
		  "error": "Unauthorized",
		  "timestamp": 1777064768.1388383,
		  "path": "/api/auth/logout"
		}""" ) ) )
	public ResponseEntity< MessageResponse > logout ( @Parameter ( name = "Authorization", description = "Bearer JWT token obtained from login or register. Format: `Bearer <token>`", required = true, in = ParameterIn.HEADER, example = "Bearer eyJhbGciOiJIUzUxMi..." ) @RequestHeader ( name = "Authorization" ) String authHeader ) {
		userService.logout(authHeader);
		return ResponseEntity.ok(new MessageResponse(LOGOUT_SUCCESSFUL));
	}

	/**
	 * Checks if the provided JWT is valid and returns the associated user data.
	 * This is useful for frontends to verify a session on page refresh.
	 */
	@GetMapping ( "/check-token" )
	@Operation ( summary = "Check token validity", description = "Verifies if the provided JWT token is valid, not expired, and not blacklisted. " + "Returns the associated user profile if valid. Useful for frontend session checks on page refresh." )
	@ApiResponse ( responseCode = "200", description = "Token is valid — returns user profile", content = @Content ( schema = @Schema ( implementation = TokenValidationResponse.class ), examples = @ExampleObject ( name = "Valid Token", value = """
		{
		  "isValid": true,
		  "message": "Token is valid",
		  "user": {
		    "id": 2,
		    "profilePictureUrl": "https://cdn-icons-png.flaticon.com/512/5951/5951752.png",
		    "name": "atul singh",
		    "email": "atul@email.com",
		    "phoneNumber": null,
		    "college": null,
		    "year": null,
		    "department": null,
		    "roll": null,
		    "gids": [],
		    "isVerified": true,
		    "isProfileCreated": false,
		    "isLoggedIn": true,
		    "lastLogin": 1777064931.26049
		  }
		}""" ) ) )
	@ApiResponse ( responseCode = "401", description = "Token is invalid, expired, revoked, or an error occurred", content = @Content ( schema = @Schema ( implementation = TokenValidationResponse.class ), examples = {@ExampleObject ( name = "Token Revoked", value = """
		{
		  "isValid": false,
		  "message": "JWT token has been revoked"
		}""" ), @ExampleObject ( name = "Error Validating Token", value = """
		{
		  "message": "Error validating token",
		  "valid": false
		}""" )} ) )
	public ResponseEntity< TokenValidationResponse > checkTokenValidity ( @Parameter ( name = "Authorization", description = "Bearer JWT token to validate. Format: `Bearer <token>`", required = true, in = ParameterIn.HEADER, example = "Bearer eyJhbGciOiJIUzUxMi..." ) @RequestHeader ( name = "Authorization" ) String authHeader ) {
		TokenValidationResponse response = userService.validateCurrentToken(authHeader);
		if ( response.isValid() ) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(UNAUTHORIZED).body(response);
		}
	}

}