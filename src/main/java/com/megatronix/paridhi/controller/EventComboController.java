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
@Tag ( name = "Event Combo", description = "Endpoints for managing and viewing event combinations/packages. Includes public access for combo details and admin access for management." )
public class EventComboController {

	private final EventComboService eventComboService;

	public EventComboController (
		EventComboService eventComboService
	) {
		this.eventComboService = eventComboService;
	}

	// Public endpoints

	@GetMapping ( "/{eventComboId}" )
	@Operation (
		summary = "Get event combo by ID",
		description = "Retrieves detailed public information for a single event combo by its unique identifier."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved combo details",
		content = @Content ( schema = @Schema ( implementation = EventComboPublicResponse.class ),
			examples = @ExampleObject ( name = "Combo detail", value = """
				{
				  "id": 1,
				  "domain": "CODING",
				  "name": "Mega Coding Pack",
				  "type": "COMBO",
				  "description": "Includes all coding events.",
				  "events": [
				    {
				      "id": 4,
				      "domain": "CODING",
				      "name": "Code-O-Fiesta",
				      "type": "MAIN",
				      "date": 1778839200.000000000,
				      "description": "Competitive coding event.",
				      "venue": "Lab 1",
				      "coordinatorDetails": ["John Doe: 9876543210"],
				      "posterUrl": "https://ik.imagekit.io/...",
				      "isTeamEvent": true,
				      "minPlayers": 1,
				      "maxPlayers": 4,
				      "registrationFee": 150.00,
				      "isRegistrationOpen": true
				    }
				  ],
				  "posterUrl": "https://ik.imagekit.io/...",
				  "registrationFee": 250.00,
				  "isRegistrationOpen": true
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Combo not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = @ExampleObject ( name = "Not found error", value = """
				{
				  "status": 404,
				  "message": "Event combo not found.",
				  "error": "Not Found",
				  "timestamp": 1778839200.000000000,
				  "path": "/api/event-combos/999"
				}""" ) ) )
	public ResponseEntity< EventComboPublicResponse > getEventComboById (
		@Parameter ( description = "Unique identifier of the combo", example = "1" )
		@PathVariable ( name = "eventComboId" ) Long id
	) {
		return ResponseEntity.ok(eventComboService.getEventComboById(id));
	}

	// Authorized endpoints


	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get all event combos (Super Admin)",
		description = "Retrieves all event combos grouped by domain. Each object contains a domain key and a list of combos. Restricted to Super Admin only."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved categorized combos",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = CategorizedEventComboResponse.class ) ),
			examples = @ExampleObject ( name = "Categorized combos", value = """
				[
				  {
				    "domain": "CODING",
				    "eventCombos": [
				      {
				        "id": 1,
				        "domain": "CODING",
				        "name": "Mega Coding Pack",
				        "type": "COMBO",
				        "description": "Includes all coding events.",
				        "posterUrl": "https://ik.imagekit.io/...",
				        "registrationFee": 250.00,
				        "isRegistrationOpen": true
				      }
				    ]
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions — Super Admin required",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< CategorizedEventComboResponse > > getEventCombos () {
		return ResponseEntity.ok(eventComboService.getAllEventCombos());
	}


	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/domains/{domain}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get combos by domain (Super Admin)",
		description = "Retrieves a flat list of event combos belonging to a specific domain. Restricted to Super Admin only."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved combos for the domain",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = EventComboPublicResponse.class ) ),
			examples = @ExampleObject ( name = "Combos by domain", value = """
				[
				  {
				    "id": 1,
				    "domain": "CODING",
				    "name": "Mega Coding Pack",
				    "type": "COMBO",
				    "description": "Includes all coding events.",
				    "posterUrl": "https://ik.imagekit.io/...",
				    "registrationFee": 250.00,
				    "isRegistrationOpen": true
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions — Super Admin required",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< EventComboPublicResponse > > getEventCombosByDomain (
		@Parameter ( description = "Domain to filter by", example = "CODING" ) @PathVariable Domain domain
	) {
		return ResponseEntity.ok(eventComboService.getEventCombosByDomain(domain));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/status" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get combos by registration status (Super Admin)",
		description = "Retrieves event combos where registration is open or closed, grouped by domain. Restricted to Super Admin only."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved combos by status",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = CategorizedEventComboResponse.class ) ),
			examples = @ExampleObject ( name = "Open combos", value = """
				[
				  {
				    "domain": "CODING",
				    "eventCombos": [
				      {
				        "id": 1,
				        "domain": "CODING",
				        "name": "Mega Coding Pack",
				        "type": "COMBO",
				        "description": "Includes all coding events.",
				        "posterUrl": "https://ik.imagekit.io/...",
				        "registrationFee": 250.00,
				        "isRegistrationOpen": true
				      }
				    ]
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions — Super Admin required",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< CategorizedEventComboResponse > > getEventCombosByStatus (
		@Parameter ( description = "Whether to filter by open or closed registration", example = "true" )
		@RequestParam ( name = "isOpen" ) boolean isOpen
	) {
		return ResponseEntity.ok(eventComboService.getEventCombosByStatus(isOpen));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Create a new event combo",
		description = "Adds a new event combination package. Must include at least 2 event IDs. Restricted to Admin and Super Admin users."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody (
		content = @Content ( schema = @Schema ( implementation = EventComboRequest.class ),
			examples = @ExampleObject ( name = "Create combo request", value = """
				{
				  "name": "Mega Coding Pack",
				  "description": "A package containing all major coding events.",
				  "domain": "CODING",
				  "eventIds": [14, 15],
				  "registrationFee": 250.00
				}""" ) ) )
	@ApiResponse ( responseCode = "201", description = "Combo successfully created",
		content = @Content ( schema = @Schema ( implementation = EventComboAdminResponse.class ),
			examples = @ExampleObject ( name = "Created combo", value = """
				{
				  "id": 2,
				  "domain": "CODING",
				  "name": "Mega Coding Pack",
				  "type": "COMBO",
				  "description": "A package containing all major coding events.",
				  "events": [
				    {
				      "id": 14,
				      "domain": "CODING",
				      "name": "ProbleMatrix",
				      "type": "MAIN",
				      "date": 1778839200.000000000,
				      "description": "Algorithm contest.",
				      "venue": "MSIT Campus",
				      "isTeamEvent": false,
				      "minPlayers": 1,
				      "maxPlayers": 1,
				      "registrationFee": 100.00,
				      "isRegistrationOpen": false
				    },
				    {
				      "id": 15,
				      "domain": "CODING",
				      "name": "CipherSiege",
				      "type": "MAIN",
				      "date": 1778839200.000000000,
				      "description": "Security coding.",
				      "venue": "MSIT Campus",
				      "isTeamEvent": false,
				      "minPlayers": 1,
				      "maxPlayers": 1,
				      "registrationFee": 100.00,
				      "isRegistrationOpen": false
				    }
				  ],
				  "posterUrl": "https://ik.imagekit.io/...",
				  "posterFileId": "69e0d6...",
				  "registrationFee": 250.00,
				  "isRegistrationOpen": false,
				  "createdAt": 1778839200.000000000,
				  "updatedAt": 1778839200.000000000,
				  "createdByUsername": "john@doe.com",
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation failed — fewer than 2 events or missing fields",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = @ExampleObject ( name = "Validation error", value = """
				{
				  "status": 400,
				  "message": "A combo must contain at least 2 events",
				  "error": "Bad Request",
				  "timestamp": 1778839200.000000000,
				  "path": "/api/event-combos",
				  "validationErrors": [
				    { "field": "eventIds", "message": "A combo must contain at least 2 events" }
				  ]
				}""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventComboAdminResponse > createEventCombo (
		@Valid @RequestBody EventComboRequest request,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(eventComboService.createEventCombo(request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping ( "/{eventComboId}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Update an existing event combo",
		description = "Modifies the details of an existing event combo. Restricted to Admin and Super Admin users."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody (
		content = @Content ( schema = @Schema ( implementation = EventComboRequest.class ),
			examples = @ExampleObject ( name = "Update combo request", value = """
				{
				  "name": "Updated Mega Pack",
				  "description": "Updated description for the pack.",
				  "domain": "CODING",
				  "eventIds": [14, 15, 16],
				  "registrationFee": 300.00
				}""" ) ) )
	@ApiResponse ( responseCode = "200", description = "Combo successfully updated",
		content = @Content ( schema = @Schema ( implementation = EventComboAdminResponse.class ),
			examples = @ExampleObject ( name = "Updated combo", value = """
				{
				  "id": 2,
				  "domain": "CODING",
				  "name": "Updated Mega Pack",
				  "type": "COMBO",
				  "description": "Updated description for the pack.",
				  "events": [
				    { "id": 14, "name": "ProbleMatrix" },
				    { "id": 15, "name": "CipherSiege" },
				    { "id": 16, "name": "Code Warriors" }
				  ],
				  "posterUrl": "https://ik.imagekit.io/...",
				  "posterFileId": "69e0d6...",
				  "registrationFee": 300.00,
				  "isRegistrationOpen": false,
				  "createdAt": 1778839200.000000000,
				  "updatedAt": 1778839200.000000000,
				  "createdByUsername": "john@doe.com",
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Combo not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventComboAdminResponse > updateEventCombo (
		@Parameter ( description = "Unique identifier of the combo to update", example = "1" )
		@PathVariable ( name = "eventComboId" ) Long id,
		@Valid @RequestBody EventComboRequest request,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.updateEventCombo(id, request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{eventComboId}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Delete an event combo",
		description = "Permanently removes an event combo package from the portal. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "204", description = "Combo successfully deleted — no content returned" )
	@ApiResponse ( responseCode = "404", description = "Combo not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< Void > deleteEventCombo (
		@Parameter ( description = "Unique identifier of the combo to delete", example = "1" )
		@PathVariable ( name = "eventComboId" ) Long id,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		eventComboService.deleteEventCombo(id, user);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{eventComboId}/status" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Toggle combo registration status",
		description = "Toggles the registration status (open/closed) of an event combo. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Combo status successfully toggled",
		content = @Content ( schema = @Schema ( implementation = EventComboAdminResponse.class ),
			examples = @ExampleObject ( name = "Status toggled", value = """
				{
				  "id": 2,
				  "domain": "CODING",
				  "name": "Mega Coding Pack",
				  "type": "COMBO",
				  "isRegistrationOpen": true,
				  "createdAt": 1778839200.000000000,
				  "updatedAt": 1778839200.000000000,
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Combo not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventComboAdminResponse > toggleEventComboRegistrationStatus (
		@Parameter ( description = "Unique identifier of the combo", example = "1" )
		@PathVariable ( name = "eventComboId" ) Long id,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.toggleEventComboRegistrationStatus(id, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping (
		value = "/{eventComboId}/upload",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Upload combo poster",
		description = "Uploads a poster image for an existing event combo. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Combo poster successfully updated",
		content = @Content ( schema = @Schema ( implementation = EventComboAdminResponse.class ),
			examples = @ExampleObject ( name = "Updated poster", value = """
				{
				  "id": 2,
				  "domain": "CODING",
				  "name": "Mega Coding Pack",
				  "type": "COMBO",
				  "posterUrl": "https://ik.imagekit.io/updated_combo.jpg",
				  "posterFileId": "69e0d6...",
				  "updatedAt": 1778839200.000000000,
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Combo not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventComboAdminResponse > updateEventComboImage (
		@Parameter ( description = "Unique identifier of the combo", example = "1" )
		@PathVariable ( name = "eventComboId" ) Long id,
		@Parameter ( description = "Image file to upload", schema = @Schema ( type = "string", format = "binary" ) )
		@RequestParam ( "file" ) MultipartFile file,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.updateEventComboImage(id, file, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ( "/{eventComboId}/events/{eventId}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Add an event to a combo",
		description = "Adds a single event to an existing combo package. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Event successfully added to combo",
		content = @Content ( schema = @Schema ( implementation = EventComboAdminResponse.class ),
			examples = @ExampleObject ( name = "Event added", value = """
				{
				  "id": 2,
				  "domain": "CODING",
				  "name": "Mega Coding Pack",
				  "type": "COMBO",
				  "events": [
				    { "id": 14, "name": "ProbleMatrix" },
				    { "id": 15, "name": "CipherSiege" },
				    { "id": 16, "name": "Code Warriors" }
				  ],
				  "updatedAt": 1778839200.000000000,
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Combo or event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventComboAdminResponse > addSingleEventToEventCombo (
		@Parameter ( description = "ID of the combo", example = "1" ) @PathVariable Long eventComboId,
		@Parameter ( description = "ID of the event to add", example = "5" ) @PathVariable Long eventId,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.addSingleEventToEventCombo(eventComboId, eventId, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{eventComboId}/events/{eventId}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Remove an event from a combo",
		description = "Removes a single event from an existing combo package. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Event successfully removed from combo",
		content = @Content ( schema = @Schema ( implementation = EventComboAdminResponse.class ),
			examples = @ExampleObject ( name = "Event removed", value = """
				{
				  "id": 2,
				  "domain": "CODING",
				  "name": "Mega Coding Pack",
				  "type": "COMBO",
				  "events": [
				    { "id": 14, "name": "ProbleMatrix" },
				    { "id": 15, "name": "CipherSiege" }
				  ],
				  "updatedAt": 1778839200.000000000,
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Combo or event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventComboAdminResponse > removeSingleEventFromEventCombo (
		@Parameter ( description = "ID of the combo", example = "1" ) @PathVariable Long eventComboId,
		@Parameter ( description = "ID of the event to remove", example = "5" ) @PathVariable Long eventId,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.removeSingleEventFromEventCombo(eventComboId, eventId, user));
	}
}
