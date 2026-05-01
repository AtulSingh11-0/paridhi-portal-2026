package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.request.AdminMRDRequest;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.MRDResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.MRDService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ( "/api/mrd" )
@Tag ( name = "MRD", description = "Endpoints for managing Maximum Registration Desk (MRD) registrations and Global IDs (GIDs)" )
public class MRDController {
	private final MRDService mrdService;

	// Public Endpoints

	//  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
	@PostMapping ( "/register" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Register user for MRD",
		description = "Registers the currently authenticated user for an MRD and generates a new GID."
	)
	@ApiResponse ( responseCode = "201", description = "Successfully registered and generated GID",
		content = @Content ( schema = @Schema ( implementation = MRDResponse.class ),
			examples = @ExampleObject ( name = "MRD Registered", value = """
				{
				  "gid": "PD26-0001-AB"
				}""" ) ) )
	@ApiResponse ( responseCode = "401", description = "Unauthorized",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< MRDResponse > registerMRD (
		@Parameter ( description = "Bearer Authorization token", example = "Bearer <token>" )
		@RequestHeader ( name = "Authorization" ) String authHeader
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(mrdService.performUserMRD(authHeader));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ( "/admin/register" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Register user for MRD by Admin",
		description = "Allows an Admin or Super Admin to register a user for an MRD by providing their details and generates a new GID."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody (
		content = @Content ( schema = @Schema ( implementation = AdminMRDRequest.class ),
			examples = @ExampleObject ( name = "Admin MRD Request", value = """
				{
				  "email": "user@example.com",
				  "name": "Jane Doe",
				  "contact": "9876543210",
				  "college": "MSIT",
				  "year": "FIRST",
				  "department": "CSE",
				  "rollNo": "12345678901"
				}""" ) ) )
	@ApiResponse ( responseCode = "201", description = "Successfully registered and generated GID",
		content = @Content ( schema = @Schema ( implementation = MRDResponse.class ),
			examples = @ExampleObject ( name = "MRD Registered by Admin", value = """
				{
				  "gid": "PD26-0001-AB"
				}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation failed",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< MRDResponse > registerMRDByAdmin (
		@Valid @RequestBody AdminMRDRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(mrdService.performUserMRDByAdmin(request));
	}


	// Authorized endpoints

	@PreAuthorize ( "hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/user/{email}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get user MRDs by email",
		description = "Retrieves a list of all MRD responses (GIDs) for a specific user email."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved MRDs",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = MRDResponse.class ) ),
			examples = @ExampleObject ( name = "User MRDs", value = """
				[
				  {
				    "gid": "PD26-0001-AB"
				  },
				  {
				    "gid": "PD26-0001-AB"
				  }
				]""" ) ) )
	public ResponseEntity< List< MRDResponse > > getUserMRDs (
		@Parameter ( description = "Email of the user", example = "user@example.com" ) @PathVariable String email,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.getUserMRDs(email, user));
	}

	@PreAuthorize ( "hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/user/{email}/gids" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get user GIDs by email",
		description = "Retrieves a list of all raw GIDs for a specific user email."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved GIDs",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( type = "string" ) ),
			examples = @ExampleObject ( name = "User GIDs", value = """
				[
				  "PD26-0001-AB",
				  "PD26-0001-AB"
				]""" ) ) )
	public ResponseEntity< List< String > > getUserGids (
		@Parameter ( description = "Email of the user", example = "user@example.com" ) @PathVariable String email,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.getUserGids(email, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/{gid}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get MRD details by GID",
		description = "Retrieves the MRD details for a specific GID. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved MRD details",
		content = @Content ( schema = @Schema ( implementation = MRDResponse.class ),
			examples = @ExampleObject ( name = "MRD Details", value = """
				{
				  "gid": "PD26-0001-AB"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "MRD not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< MRDResponse > getMRDbyGID (
		@Parameter ( description = "Global ID of the MRD", example = "PD-2026-0001" ) @PathVariable String gid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.getMRDbyGID(gid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "{gid}/payment" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Update MRD payment status",
		description = "Updates the payment status for an MRD. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully updated payment status",
		content = @Content ( schema = @Schema ( implementation = MRDResponse.class ),
			examples = @ExampleObject ( name = "MRD Payment Updated", value = """
				{
				  "gid": "PD26-0001-AB"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "MRD not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< MRDResponse > updatePaymentStatus (
		@Parameter ( description = "Global ID of the MRD", example = "PD-2026-0001" ) @PathVariable String gid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.updatePaymentStatus(gid, user));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get all MRDs (Super Admin)",
		description = "Retrieves a list of all MRDs. Restricted to Super Admin only."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved all MRDs",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = MRDResponse.class ) ),
			examples = @ExampleObject ( name = "All MRDs", value = """
				[
				  {
				    "gid": "PD26-0001-AB"
				  },
				  {
				    "gid": "PD26-0001-AB"
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions — Super Admin required",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< MRDResponse > > getAllMRDs (
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.getAllMRDs(user));
	}
}
