package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.request.ProfileRequest;
import com.megatronix.paridhi.dto.request.ProfileUpdateRequest;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.ProfileResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping ( "/api/profiles" )
@Tag ( name = "Profile", description = "Endpoints for creating, reading, and updating user profiles. All endpoints require authentication via JWT Bearer token." )
@SecurityRequirement ( name = "Bearer Authentication" )
public class ProfileController {
	
	private final ProfileService profileService;

	public ProfileController ( ProfileService profileService ) {
		this.profileService = profileService;
	}

	@PreAuthorize ( "isAuthenticated()" )
	@PostMapping
	@Operation ( summary = "Create a user profile", description = """
		Creates a profile for the authenticated user by filling in phone number, college, year, department, and roll number.
		**IDOR Protection**: The email in the request body must match the authenticated user's email.
		**Security Requirement**: The user must be email-verified before creating a profile.
		**Business Rule**: A user can only create one profile; subsequent attempts will return a 409 Conflict.
		""" )
	@ApiResponse ( responseCode = "201", description = "Profile created successfully", content = @Content ( schema = @Schema ( implementation = ProfileResponse.class ), examples = @ExampleObject ( value = """
		{
		  "profileDetails": {
		    "id": 1,
		    "profilePictureUrl": "https://cdn-icons-png.flaticon.com/512/5951/5951752.png",
		    "name": "John Doe",
		    "email": "john@doe.com",
		    "phoneNumber": "+9876543210",
		    "college": "IIT Delhi",
		    "year": "FOURTH",
		    "department": "CSE",
		    "roll": "ADM001",
		    "gids": [],
		    "isVerified": true,
		    "isProfileCreated": true,
		    "isLoggedIn": true,
		    "lastLogin": 1714073334.1234567
		  }
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation error — missing or invalid fields", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = {
		@ExampleObject ( name = "Missing Fields", value = """
			{
			  "status": 400,
			  "message": "Validation error",
			  "error": "Bad Request",
			  "timestamp": 1714073334.1234567,
			  "path": "/api/profiles",
			  "validationErrors": [
			    {"field": "email", "message": "Email cannot be empty"},
			    {"field": "phoneNumber", "message": "Phone number cannot be empty"},
			    {"field": "college", "message": "College name cannot be empty"},
			    {"field": "year", "message": "Year cannot be null"},
			    {"field": "department", "message": "Department cannot be null"},
			    {"field": "rollNo", "message": "Roll number cannot be empty"}
			  ]
			}""" ),
		@ExampleObject ( name = "Invalid Phone Format", value = """
			{
			  "status": 400,
			  "message": "Validation error",
			  "error": "Bad Request",
			  "timestamp": 1714073334.1234567,
			  "path": "/api/profiles",
			  "validationErrors": [
			    {"field": "phoneNumber", "message": "Phone Number must be between 10 and 15 characters, optionally starting with '+'"}
			  ]
			}""" ),
		@ExampleObject ( name = "Invalid Enum Value", value = """
			{
			  "status": 400,
			  "message": "Malformed JSON request: JSON parse error: Cannot deserialize value of type `Year` from String \\"INVALID\\": not one of the values accepted for Enum class: [FIRST, SECOND, THIRD, FOURTH, PASSOUT, ALUMNI, OTHER, OTHERS, NOT_APPLICABLE]",
			  "error": "Bad Request",
			  "timestamp": 1714073334.1234567,
			  "path": "/api/profiles"
			}""" )
	} ) )
	@ApiResponse ( responseCode = "401", description = "Missing or invalid JWT token", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 401,
		  "error": "Unauthorized",
		  "message": "Full authentication is required to access this resource",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles"
		}""" ) ) )
	@ApiResponse ( responseCode = "403", description = "User is unverified or attempting to create profile for another user", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = {
		@ExampleObject ( name = "Unverified User", value = """
			{
			  "status": 403,
			  "message": "User email must be verified to create a profile",
			  "error": "Forbidden",
			  "timestamp": 1714073334.1234567,
			  "path": "/api/profiles"
			}""" ),
		@ExampleObject ( name = "IDOR Attempt (Email Mismatch)", value = """
			{
			  "status": 403,
			  "message": "You don't have permission to access this resource.",
			  "error": "Forbidden",
			  "timestamp": 1714073334.1234567,
			  "path": "/api/profiles"
			}""" )
	} ) )
	@ApiResponse ( responseCode = "409", description = "Profile already exists for this user", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 409,
		  "message": "The request could not be completed due to a conflict with current state.",
		  "error": "Conflict",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles"
		}""" ) ) )
	public ResponseEntity< ProfileResponse > createProfile (
		@Valid @RequestBody ProfileRequest request,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(CREATED).body(profileService.createProfile(request, user));
	}

	@PreAuthorize ( "isAuthenticated()" )
	@GetMapping ( "/{id}" )
	@Operation ( summary = "Get a user profile by ID", description = """
		Retrieves a user's profile details by their internal user ID.
		**Access Control**: Regular users can only access their own profile. Admins and Super Admins have full read access to all profiles.
		""" )
	@ApiResponse ( responseCode = "200", description = "Profile retrieved successfully", content = @Content ( schema = @Schema ( implementation = ProfileResponse.class ), examples = @ExampleObject ( value = """
		{
		  "profileDetails": {
		    "id": 1,
		    "profilePictureUrl": "https://cdn-icons-png.flaticon.com/512/5951/5951752.png",
		    "name": "John Doe",
		    "email": "john@doe.com",
		    "phoneNumber": "+1111111111",
		    "college": "NIT Trichy",
		    "year": "THIRD",
		    "department": "ECE",
		    "roll": "ECE002",
		    "gids": [],
		    "isVerified": true,
		    "isProfileCreated": true,
		    "isLoggedIn": false,
		    "lastLogin": 1714073334.1234567
		  }
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Invalid ID format (non-numeric)", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 400,
		  "message": "Invalid parameter type. Expected Long but got abc",
		  "error": "Bad Request",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles/abc"
		}""" ) ) )
	@ApiResponse ( responseCode = "401", description = "Missing or invalid JWT token", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 401,
		  "error": "Unauthorized",
		  "message": "Full authentication is required to access this resource",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles/1"
		}""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Access denied — regular user attempting to view another's profile", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 403,
		  "message": "You don't have permission to access this resource.",
		  "error": "Forbidden",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles/1"
		}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "User not found", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 404,
		  "message": "User not found",
		  "error": "Not Found",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles/99999"
		}""" ) ) )
	public ResponseEntity< ProfileResponse > getProfileById (
		@Parameter ( description = "User ID", example = "1" ) @PathVariable Long id,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(profileService.getProfileById(id, user));
	}

	@PreAuthorize ( "isAuthenticated()" )
	@PutMapping ( "/{id}" )
	@Operation ( summary = "Update a user profile", description = """
		Updates an existing user profile with new phone number, college, year, department, and roll number.
		**Guard**: The profile must have been created before it can be updated.
		**Access Control**: Regular users can only update their own profile. Admins and Super Admins can update any profile.
		""" )
	@ApiResponse ( responseCode = "200", description = "Profile updated successfully", content = @Content ( schema = @Schema ( implementation = ProfileResponse.class ), examples = @ExampleObject ( value = """
		{
		  "profileDetails": {
		    "id": 1,
		    "profilePictureUrl": "https://cdn-icons-png.flaticon.com/512/5951/5951752.png",
		    "name": "John Doe",
		    "email": "john@doe.com",
		    "phoneNumber": "+1111111111",
		    "college": "NIT Trichy",
		    "year": "THIRD",
		    "department": "ECE",
		    "roll": "ECE002",
		    "gids": [],
		    "isVerified": true,
		    "isProfileCreated": true,
		    "isLoggedIn": true,
		    "lastLogin": 1714073334.1234567
		  }
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation error or profile not yet created", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = {
		@ExampleObject ( name = "Profile Not Created", value = """
			{
			  "status": 400,
			  "message": "User needs to create a profile first",
			  "error": "Bad Request",
			  "timestamp": 1714073334.1234567,
			  "path": "/api/profiles/5"
			}""" ),
		@ExampleObject ( name = "Validation Errors", value = """
			{
			  "status": 400,
			  "message": "Validation error",
			  "error": "Bad Request",
			  "timestamp": 1714073334.1234567,
			  "path": "/api/profiles/5",
			  "validationErrors": [
			    {"field": "phoneNumber", "message": "Phone number cannot be empty"},
			    {"field": "college", "message": "College name cannot be empty"},
			    {"field": "year", "message": "Year cannot be null"},
			    {"field": "department", "message": "Department cannot be null"},
			    {"field": "rollNo", "message": "Roll number cannot be empty"}
			  ]
			}""" )
	} ) )
	@ApiResponse ( responseCode = "401", description = "Missing or invalid JWT token", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 401,
		  "error": "Unauthorized",
		  "message": "Full authentication is required to access this resource",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles/5"
		}""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Access denied — regular user attempting to update another's profile", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 403,
		  "message": "You don't have permission to access this resource.",
		  "error": "Forbidden",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles/1"
		}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "User not found", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 404,
		  "message": "User not found",
		  "error": "Not Found",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles/999"
		}""" ) ) )
	public ResponseEntity< ProfileResponse > updateProfile (
		@Parameter ( description = "User ID", example = "1" ) @PathVariable Long id,
		@Valid @RequestBody ProfileUpdateRequest request,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(profileService.updateProfile(id, request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping
	@Operation ( summary = "List all profiles by status", description = """
		Returns a list of all user profiles filtered by profile creation status.
		**Role Restriction**: This endpoint is restricted to users with ADMIN or SUPERADMIN roles. Regular users will receive a 403 Forbidden.
		Use the `profile-created` query parameter to toggle between users with completed profiles and those without.
		""" )
	@ApiResponse ( responseCode = "200", description = "Profiles retrieved successfully", content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = ProfileResponse.class ) ), examples = @ExampleObject ( value = """
		[
		  {
		    "profileDetails": {
		      "id": 3,
		      "profilePictureUrl": "https://cdn-icons-png.flaticon.com/512/5951/5951752.png",
		      "name": "Atul Singh",
		      "email": "cocatul11@gmail.com",
		      "phoneNumber": "9123766255",
		      "college": "MSIT",
		      "year": "THIRD",
		      "department": "BCA",
		      "roll": "384",
		      "gids": [],
		      "isVerified": true,
		      "isProfileCreated": true,
		      "isLoggedIn": false,
		      "lastLogin": 1714073334.1234567
		    }
		  }
		]""" ) ) )
	@ApiResponse ( responseCode = "401", description = "Missing or invalid JWT token", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 401,
		  "error": "Unauthorized",
		  "message": "Full authentication is required to access this resource",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles"
		}""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Access denied — regular user attempting to list all profiles", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ), examples = @ExampleObject ( value = """
		{
		  "status": 403,
		  "message": "You don't have permission to access this resource.",
		  "error": "Forbidden",
		  "timestamp": 1714073334.1234567,
		  "path": "/api/profiles"
		}""" ) ) )
	public ResponseEntity< List< ProfileResponse > > getAllByIsProfileCreated (
		@Parameter ( description = "Filter by profile creation status", example = "true" ) @RequestParam ( name = "profile-created", defaultValue = "true" ) boolean isProfileCreated,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(profileService.getAllByIsProfileCreated(isProfileCreated, user));
	}
}
