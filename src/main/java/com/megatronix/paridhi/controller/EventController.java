package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import com.megatronix.paridhi.dto.request.EventRequest;
import com.megatronix.paridhi.dto.response.*;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.EventService;
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
@RequestMapping ( "/api/events" )
@Tag ( name = "Event", description = "Endpoints for managing and viewing events. Includes public access for event details and admin access for management." )
public class EventController {

	private final EventService eventService;

	public EventController ( EventService eventService ) {
		this.eventService = eventService;
	}

	// Public endpoints

	@GetMapping ( "/combos" )
	@Operation (
		summary = "Get all events and event combos categorized by domain",
		description = "Retrieves a list of domains, each containing its respective events and event combos. This is the primary public endpoint for the frontend home/events page."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved events and event combos",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = CategorizedEventAndEventComboResponse.class ) ),
			examples = @ExampleObject ( name = "Full categorized response", value = """
				[
				  {
				    "domain": "CODING",
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
				    "eventCombos": [
				      {
				        "id": 1,
				        "domain": "CODING",
				        "name": "Coding Master Combo",
				        "description": "Access to all coding events",
				        "posterUrl": "https://ik.imagekit.io/...",
				        "registrationFee": 400.00,
				        "isRegistrationOpen": true,
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
				        ]
				      }
				    ]
				  }
				]""" ) ) )
	public ResponseEntity< List< CategorizedEventAndEventComboResponse > > getAllEventsAndCombos () {
		return ResponseEntity.ok(eventService.getAllEventsAndCombos());
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ()
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get all events (Super Admin)",
		description = "Retrieves all events grouped by domain. Restricted to Super Admin only."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved categorized events",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = CategorizedEventResponse.class ) ),
			examples = @ExampleObject ( name = "Categorized events", value = """
				[
				  {
				    "domain": "CODING",
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
				    ]
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions — Super Admin required",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< CategorizedEventResponse > > getAllEvents () {
		return ResponseEntity.ok(eventService.getAllEvents());
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/domains/{domain}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get events by domain (Super Admin)",
		description = "Retrieves a flat list of events belonging to a specific domain. Restricted to Super Admin only."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved events for the domain",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = EventPublicResponse.class ) ),
			examples = @ExampleObject ( name = "Events by domain", value = """
				[
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
				]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions — Super Admin required",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< EventPublicResponse > > getEventsByDomain (
		@Parameter ( description = "Domain to filter by", example = "CODING" ) @PathVariable Domain domain
	) {
		return ResponseEntity.ok(eventService.getEventsByDomain(domain));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/status" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get events by registration status (Super Admin)",
		description = "Retrieves events where registration is either open or closed, grouped by domain. Restricted to Super Admin only."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved events by registration status",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = CategorizedEventResponse.class ) ),
			examples = @ExampleObject ( name = "Open events", value = """
				[
				  {
				    "domain": "CODING",
				    "events": [
				      {
				        "id": 4,
				        "domain": "CODING",
				        "name": "Code-O-Fiesta",
				        "type": "MAIN",
				        "date": 1778839200.000000000,
				        "description": "Competitive coding event.",
				        "venue": "Lab 1",
				        "isTeamEvent": true,
				        "isRegistrationOpen": true
				      }
				    ]
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions — Super Admin required",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< CategorizedEventResponse > > getEventsByStatus (
		@Parameter ( description = "Whether to filter by open or closed registration", example = "true" )
		@RequestParam ( name = "isOpen" ) boolean isOpen,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventService.getEventsByRegistration(isOpen, user));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/type/{eventType}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get events by type (Super Admin)",
		description = "Retrieves events of a specific type (e.g., MAIN, ON_SPOT), grouped by domain. Restricted to Super Admin only."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved events by type",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = CategorizedEventResponse.class ) ),
			examples = @ExampleObject ( name = "MAIN events", value = """
				[
				  {
				    "domain": "CODING",
				    "events": [
				      {
				        "id": 4,
				        "domain": "CODING",
				        "name": "Code-O-Fiesta",
				        "type": "MAIN",
				        "date": 1778839200.000000000,
				        "description": "Competitive coding event.",
				        "venue": "Lab 1",
				        "isTeamEvent": true,
				        "isRegistrationOpen": true
				      }
				    ]
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions — Super Admin required",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< CategorizedEventResponse > > getEventsByType (
		@Parameter ( description = "Type of event to filter by", example = "MAIN" ) @PathVariable EventType eventType
	) {
		return ResponseEntity.ok(eventService.getEventsByType(eventType));
	}

	@GetMapping ( "/{id}" )
	@Operation (
		summary = "Get event by ID",
		description = "Retrieves detailed public information for a single event by its unique identifier."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved event details",
		content = @Content ( schema = @Schema ( implementation = EventPublicResponse.class ),
			examples = @ExampleObject ( name = "Event detail", value = """
				{
				  "id": 1,
				  "domain": "GAMING",
				  "name": "BGMI",
				  "type": "MAIN",
				  "date": 1778839200.000000000,
				  "description": "Squad battle royale.",
				  "venue": "MSIT Campus",
				  "coordinatorDetails": ["Aditya Kumar - 9823456789"],
				  "posterUrl": "https://ik.imagekit.io/...",
				  "isTeamEvent": true,
				  "minPlayers": 4,
				  "maxPlayers": 5,
				  "registrationFee": 150.00,
				  "isRegistrationOpen": false
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = @ExampleObject ( name = "Not found error", value = """
				{
				  "status": 404,
				  "message": "Event not found.",
				  "error": "Not Found",
				  "timestamp": 1778839200.000000000,
				  "path": "/api/events/999"
				}""" ) ) )
	public ResponseEntity< EventPublicResponse > getEventById (
		@Parameter ( description = "Unique identifier of the event", example = "1" ) @PathVariable Long id
	) {
		return ResponseEntity.ok(eventService.getEventById(id));
	}

	// Authorized endpoints

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ()
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Create a new event",
		description = "Adds a new event to the portal. Restricted to Admin and Super Admin users."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody (
		content = @Content ( schema = @Schema ( implementation = EventRequest.class ),
			examples = @ExampleObject ( name = "Create event request", value = """
				{
				  "domain": "CODING",
				  "name": "Code-O-Fiesta",
				  "type": "MAIN",
				  "date": 1778839200.000000000,
				  "description": "Competitive coding event for students.",
				  "venue": "Lab 1",
				  "coordinatorDetails": ["John Doe: 9876543210"],
				  "ruleBookUrl": "http://example.com/rulebook.pdf",
				  "isTeamEvent": true,
				  "minPlayers": 1,
				  "maxPlayers": 4,
				  "registrationFee": 150.00
				}""" ) ) )
	@ApiResponse ( responseCode = "201", description = "Event successfully created",
		content = @Content ( schema = @Schema ( implementation = EventAdminResponse.class ),
			examples = @ExampleObject ( name = "Created event", value = """
				{
				  "id": 28,
				  "domain": "CODING",
				  "name": "Code-O-Fiesta",
				  "type": "MAIN",
				  "date": 1778839200.000000000,
				  "description": "Competitive coding event for students.",
				  "venue": "Lab 1",
				  "coordinatorDetails": ["John Doe: 9876543210"],
				  "posterUrl": "https://ik.imagekit.io/...",
				  "posterFileId": "69e0d6...",
				  "ruleBookUrl": "http://example.com/rulebook.pdf",
				  "isTeamEvent": true,
				  "minPlayers": 1,
				  "maxPlayers": 4,
				  "registrationFee": 150.00,
				  "isRegistrationOpen": false,
				  "createdAt": 1778839200.000000000,
				  "updatedAt": 1778839200.000000000,
				  "createdByUsername": "john@doe.com",
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation failed — missing or invalid fields",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = @ExampleObject ( name = "Validation error", value = """
				{
				  "status": 400,
				  "message": "Event name cannot be empty",
				  "error": "Bad Request",
				  "timestamp": 1778839200.000000000,
				  "path": "/api/events",
				  "validationErrors": [
				    { "field": "name", "message": "Event name cannot be empty" }
				  ]
				}""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Insufficient permissions",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventAdminResponse > createEvent (
		@Valid @RequestBody EventRequest request,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping ( "/{id}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Update an existing event",
		description = "Modifies the details of an existing event. Restricted to Admin and Super Admin users."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody (
		content = @Content ( schema = @Schema ( implementation = EventRequest.class ),
			examples = @ExampleObject ( name = "Update event request", value = """
				{
				  "domain": "CODING",
				  "name": "Updated Code-O-Fiesta",
				  "type": "MAIN",
				  "date": 1778839200.000000000,
				  "description": "Updated description for the coding event.",
				  "venue": "Lab 2",
				  "coordinatorDetails": ["John Doe: 9876543210"],
				  "ruleBookUrl": "http://example.com/rulebook_v2.pdf",
				  "isTeamEvent": true,
				  "minPlayers": 1,
				  "maxPlayers": 4,
				  "registrationFee": 200.00
				}""" ) ) )
	@ApiResponse ( responseCode = "200", description = "Event successfully updated",
		content = @Content ( schema = @Schema ( implementation = EventAdminResponse.class ),
			examples = @ExampleObject ( name = "Updated event", value = """
				{
				  "id": 28,
				  "domain": "CODING",
				  "name": "Updated Code-O-Fiesta",
				  "type": "MAIN",
				  "date": 1778839200.000000000,
				  "description": "Updated description for the coding event.",
				  "venue": "Lab 2",
				  "coordinatorDetails": ["John Doe: 9876543210"],
				  "posterUrl": "https://ik.imagekit.io/...",
				  "posterFileId": "69e0d6...",
				  "ruleBookUrl": "http://example.com/rulebook_v2.pdf",
				  "isTeamEvent": true,
				  "minPlayers": 1,
				  "maxPlayers": 4,
				  "registrationFee": 200.00,
				  "isRegistrationOpen": false,
				  "createdAt": 1778839200.000000000,
				  "updatedAt": 1778839200.000000000,
				  "createdByUsername": "john@doe.com",
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventAdminResponse > updateEvent (
		@Parameter ( description = "Unique identifier of the event to update", example = "1" ) @PathVariable Long id,
		@Valid @RequestBody EventRequest request,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventService.updateEvent(id, request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping (
		value = "/{id}/upload",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Upload event poster",
		description = "Uploads a poster image for an existing event. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Event poster successfully updated",
		content = @Content ( schema = @Schema ( implementation = EventAdminResponse.class ),
			examples = @ExampleObject ( name = "Updated poster", value = """
				{
				  "id": 28,
				  "domain": "CODING",
				  "name": "Code-O-Fiesta",
				  "posterUrl": "https://ik.imagekit.io/updated_poster.jpg",
				  "posterFileId": "69e0d6...",
				  "isRegistrationOpen": false,
				  "updatedAt": 1778839200.000000000,
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventAdminResponse > updateEventImage (
		@Parameter ( description = "Unique identifier of the event", example = "1" ) @PathVariable Long id,
		@Parameter ( description = "Image file to upload", schema = @Schema ( type = "string", format = "binary" ) )
		@RequestParam ( "file" ) MultipartFile file,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventService.updateEventImage(id, file, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{id}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Delete an event",
		description = "Permanently removes an event from the portal. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "204", description = "Event successfully deleted — no content returned" )
	@ApiResponse ( responseCode = "404", description = "Event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ),
			examples = @ExampleObject ( name = "Not found error", value = """
				{
				  "status": 404,
				  "message": "Event not found.",
				  "error": "Not Found",
				  "timestamp": 1778839200.000000000,
				  "path": "/api/events/999"
				}""" ) ) )
	public ResponseEntity< Void > deleteEvent (
		@Parameter ( description = "Unique identifier of the event to delete", example = "1" ) @PathVariable Long id,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		eventService.deleteEvent(id, user);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{id}/status" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Toggle event registration status",
		description = "Toggles the registration status (open/closed) of an event. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Event status successfully toggled",
		content = @Content ( schema = @Schema ( implementation = EventAdminResponse.class ),
			examples = @ExampleObject ( name = "Status toggled", value = """
				{
				  "id": 28,
				  "domain": "CODING",
				  "name": "Code-O-Fiesta",
				  "isRegistrationOpen": true,
				  "createdAt": 1778839200.000000000,
				  "updatedAt": 1778839200.000000000,
				  "updatedByUsername": "john@doe.com"
				}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< EventAdminResponse > updateEventStatus (
		@Parameter ( description = "Unique identifier of the event", example = "1" ) @PathVariable Long id,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventService.toggleRegistrationStatus(id, user));
	}
}
