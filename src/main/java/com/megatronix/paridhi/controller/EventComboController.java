package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.dto.request.EventComboRequest;
import com.megatronix.paridhi.dto.response.CategorizedEventComboResponse;
import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.EventComboAdminResponse;
import com.megatronix.paridhi.dto.response.EventComboPublicResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.EventComboService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping ( "/api/event-combos" )
@Tag(name = "Event Combo", description = "Endpoints for managing and viewing event combinations/packages. Includes public access for combo details and admin access for management.")
public class EventComboController {

	private final EventComboService eventComboService;

	public EventComboController (
		EventComboService eventComboService
	) {
		this.eventComboService = eventComboService;
	}

	// Public endpoints

	@GetMapping
	@Operation(
		summary = "Get all event combos",
		description = "Retrieves a list of all event combos, grouped by their respective domains."
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved categorized combos",
		content = @Content(schema = @Schema(implementation = CategorizedEventComboResponse.class),
			examples = @ExampleObject(value = """
				{
				  "CODING": [
				    {
				      "id": 1,
				      "domain": "CODING",
				      "name": "Mega Coding Pack",
				      "description": "Includes all coding events.",
				      "posterUrl": "https://ik.imagekit.io/...",
				      "registrationFee": 250.00,
				      "isRegistrationOpen": true
				    }
				  ]
				}""")))
	public ResponseEntity< CategorizedEventComboResponse > getEventCombos () {
		return ResponseEntity.ok(eventComboService.getAllEventCombos());
	}

	@GetMapping ( "/{eventComboId}" )
	@Operation(
		summary = "Get event combo by ID",
		description = "Retrieves detailed information for a single event combo by its unique identifier."
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved combo details",
		content = @Content(schema = @Schema(implementation = EventComboPublicResponse.class),
			examples = @ExampleObject(value = """
				{
				  "id": 1,
				  "domain": "CODING",
				  "name": "Mega Coding Pack",
				  "description": "Includes all coding events.",
				  "events": [
				    {
				      "id": 4,
				      "name": "Code-O-Fiesta",
				      "date": 1778839200.000000000
				    }
				  ],
				  "posterUrl": "https://ik.imagekit.io/...",
				  "registrationFee": 250.00,
				  "isRegistrationOpen": true
				}""")))
	@ApiResponse(responseCode = "404", description = "Combo not found",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity< EventComboPublicResponse > getEventComboById (
		@Parameter(description = "Unique identifier of the combo", example = "1")
		@PathVariable ( name = "eventComboId" ) Long id
	) {
		return ResponseEntity.ok(eventComboService.getEventComboById(id));
	}

	@GetMapping ( "/domains/{domain}" )
	@Operation(
		summary = "Get combos by domain",
		description = "Retrieves a list of event combos belonging to a specific domain."
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved combos for the domain",
		content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventComboPublicResponse.class)),
			examples = @ExampleObject(value = """
				[
				  {
				    "id": 1,
				    "domain": "CODING",
				    "name": "Mega Coding Pack",
				    "registrationFee": 250.00,
				    "isRegistrationOpen": true
				  }
				]""")))
	public ResponseEntity< List< EventComboPublicResponse > > getEventCombosByDomain (
		@Parameter(description = "Domain to filter by", example = "CODING") @PathVariable Domain domain
	) {
		return ResponseEntity.ok(eventComboService.getEventCombosByDomain(domain));
	}

	@GetMapping ( "/status" )
	@Operation(
		summary = "Get combos by registration status",
		description = "Retrieves a list of event combos where registration is either open or closed, grouped by domain."
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved combos by status",
		content = @Content(schema = @Schema(implementation = CategorizedEventComboResponse.class),
			examples = @ExampleObject(value = """
				{
				  "CODING": [
				    {
				      "id": 1,
				      "domain": "CODING",
				      "name": "Mega Coding Pack",
				      "isRegistrationOpen": true
				    }
				  ]
				}""")))
	public ResponseEntity< CategorizedEventComboResponse > getEventCombosByStatus (
		@Parameter(description = "Whether to filter by open or closed registration", example = "true")
		@RequestParam ( name = "isOpen" ) boolean isOpen
	) {
		return ResponseEntity.ok(eventComboService.getEventCombosByStatus(isOpen));
	}

	// Authorized endpoints

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Create a new event combo",
		description = "Adds a new event combination package to the portal. Restricted to Admin and Super Admin users."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		content = @Content(schema = @Schema(implementation = EventComboRequest.class),
			examples = @ExampleObject(value = """
				{
				  "name": "Mega Coding Pack",
				  "description": "A package containing all major coding events.",
				  "domain": "CODING",
				  "eventIds": [14, 15],
				  "registrationFee": 250.00
				}""")))
	@ApiResponse(responseCode = "201", description = "Combo successfully created",
		content = @Content(schema = @Schema(implementation = EventComboAdminResponse.class),
			examples = @ExampleObject(value = """
				{
				  "id": 2,
				  "domain": "CODING",
				  "name": "Mega Coding Pack",
				  "description": "A package containing all major coding events.",
				  "events": [
				    { "id": 14, "name": "Problematrx", "date": 1778839200.000000000 },
				    { "id": 15, "name": "CipherSiege", "date": 1778839200.000000000 }
				  ],
				  "posterUrl": "https://ik.imagekit.io/...",
				  "posterFileId": "69e0d6...",
				  "registrationFee": 250.00,
				  "isRegistrationOpen": false,
				  "createdAt": 1778839200.000000000,
				  "updatedAt": 1778839200.000000000,
				  "createdByUsername": "john@doe.com",
				  "updatedByUsername": "john@doe.com"
				}""")))
	@ApiResponse(responseCode = "403", description = "Insufficient permissions",
		content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity< EventComboAdminResponse > createEventCombo (
		@Valid @RequestBody EventComboRequest request,
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(eventComboService.createEventCombo(request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping ( "/{eventComboId}" )
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Update an existing event combo",
		description = "Modifies the details of an existing event combo. Restricted to Admin and Super Admin users."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		content = @Content(schema = @Schema(implementation = EventComboRequest.class),
			examples = @ExampleObject(value = """
				{
				  "name": "Updated Mega Pack",
				  "description": "Updated description for the pack.",
				  "domain": "CODING",
				  "eventIds": [14, 15, 16],
				  "registrationFee": 300.00
				}""")))
	@ApiResponse(responseCode = "200", description = "Combo successfully updated",
		content = @Content(schema = @Schema(implementation = EventComboAdminResponse.class),
			examples = @ExampleObject(value = """
				{
				  "id": 2,
				  "name": "Updated Mega Pack",
				  "updatedAt": 1778839200.000000000
				}""")))
	public ResponseEntity< EventComboAdminResponse > updateEventCombo (
		@Parameter(description = "Unique identifier of the combo to update", example = "1")
		@PathVariable ( name = "eventComboId" ) Long id,
		@Valid @RequestBody EventComboRequest request,
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.updateEventCombo(id, request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{eventComboId}" )
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Delete an event combo",
		description = "Removes an event combo package from the portal. Restricted to Admin and Super Admin users."
	)
	@ApiResponse(responseCode = "204", description = "Combo successfully deleted")
	public ResponseEntity< Void > deleteEventCombo (
		@Parameter(description = "Unique identifier of the combo to delete", example = "1")
		@PathVariable ( name = "eventComboId" ) Long id,
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		eventComboService.deleteEventCombo(id, user);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{eventComboId}/status" )
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Toggle combo registration status",
		description = "Toggles the registration status (open/closed) of an event combo. Restricted to Admin and Super Admin users."
	)
	@ApiResponse(responseCode = "200", description = "Combo status successfully toggled",
		content = @Content(schema = @Schema(implementation = EventComboAdminResponse.class),
			examples = @ExampleObject(value = """
				{
				  "id": 2,
				  "isRegistrationOpen": true,
				  "updatedAt": 1778839200.000000000
				}""")))
	public ResponseEntity< EventComboAdminResponse > toggleEventComboRegistrationStatus (
		@Parameter(description = "Unique identifier of the combo", example = "1")
		@PathVariable ( name = "eventComboId" ) Long id,
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.toggleEventComboRegistrationStatus(id, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping (
		value = "/{eventComboId}/upload",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Upload combo poster",
		description = "Uploads a poster image for an existing event combo. Restricted to Admin and Super Admin users."
	)
	@ApiResponse(responseCode = "200", description = "Combo poster successfully updated",
		content = @Content(schema = @Schema(implementation = EventComboAdminResponse.class),
			examples = @ExampleObject(value = """
				{
				  "id": 2,
				  "posterUrl": "https://ik.imagekit.io/updated_combo.jpg",
				  "updatedAt": 1778839200.000000000
				}""")))
	public ResponseEntity< EventComboAdminResponse > updateEventComboImage (
		@Parameter(description = "Unique identifier of the combo", example = "1")
		@PathVariable ( name = "eventComboId" ) Long id,
		@Parameter(description = "Image file to upload", schema = @Schema(type = "string", format = "binary"))
		@RequestParam ( "file" ) MultipartFile file,
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.updateEventComboImage(id, file, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ( "/{eventComboId}/events/{eventId}" )
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Add an event to combo",
		description = "Adds a single event to an existing combo package. Restricted to Admin and Super Admin users."
	)
	@ApiResponse(responseCode = "200", description = "Event successfully added to combo",
		content = @Content(schema = @Schema(implementation = EventComboAdminResponse.class),
			examples = @ExampleObject(value = """
				{
				  "id": 2,
				  "events": [
				    { "id": 14 }, { "id": 15 }, { "id": 16 }
				  ],
				  "updatedAt": 1778839200.000000000
				}""")))
	public ResponseEntity< EventComboAdminResponse > addSingleEventToEventCombo (
		@Parameter(description = "ID of the combo", example = "1") @PathVariable Long eventComboId,
		@Parameter(description = "ID of the event to add", example = "5") @PathVariable Long eventId,
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.addSingleEventToEventCombo(eventComboId, eventId, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{eventComboId}/events/{eventId}" )
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(
		summary = "Remove an event from combo",
		description = "Removes a single event from an existing combo package. Restricted to Admin and Super Admin users."
	)
	@ApiResponse(responseCode = "200", description = "Event successfully removed from combo",
		content = @Content(schema = @Schema(implementation = EventComboAdminResponse.class),
			examples = @ExampleObject(value = """
				{
				  "id": 2,
				  "events": [
				    { "id": 14 }, { "id": 15 }
				  ],
				  "updatedAt": 1778839200.000000000
				}""")))
	public ResponseEntity< EventComboAdminResponse > removeSingleEventFromEventCombo (
		@Parameter(description = "ID of the combo", example = "1") @PathVariable Long eventComboId,
		@Parameter(description = "ID of the event to remove", example = "5") @PathVariable Long eventId,
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.removeSingleEventFromEventCombo(eventComboId, eventId, user));
	}
}
