package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import com.megatronix.paridhi.dto.request.EventRequest;
import com.megatronix.paridhi.dto.response.EventAdminResponse;
import com.megatronix.paridhi.dto.response.EventPublicResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ( "/api/events" )
@RequiredArgsConstructor
public class EventController {
	private final EventService eventService;

	// Public endpoints

	@GetMapping ()
	public ResponseEntity< Map< String, List< EventPublicResponse > > > getAllEvents () {
		return ResponseEntity.ok(eventService.getAllEvents());
	}

	@GetMapping ( "/domains/{domain}" )
	public ResponseEntity< List< EventPublicResponse > > getEventsByDomain ( @PathVariable Domain domain ) {
		return ResponseEntity.ok(eventService.getEventsByDomain(domain));
	}

	@GetMapping ( "/status" )
	public ResponseEntity< Map< String, List< EventPublicResponse > > > getEventsByStatus (
		@RequestParam ( name = "isOpen" ) boolean isOpen
	) {
		return ResponseEntity.ok(eventService.getEventsByRegistration(isOpen));
	}

	@GetMapping ( "/type/{eventType}" )
	public ResponseEntity< Map< String, List< EventPublicResponse > > > getEventsByType ( @PathVariable EventType eventType ) {
		return ResponseEntity.ok(eventService.getEventsByType(eventType));
	}

	@GetMapping ( "/{id}" )
	public ResponseEntity< EventPublicResponse > getEventById ( @PathVariable Long id ) {
		return ResponseEntity.ok(eventService.getEventById(id));
	}

	// Authorized endpoints

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ()
	public ResponseEntity< EventAdminResponse > createEvent (
		@Valid @RequestBody EventRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping ( "/{id}" )
	public ResponseEntity< EventAdminResponse > updateEvent (
		@PathVariable Long id,
		@Valid @RequestBody EventRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventService.updateEvent(id, request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping (
		value = "/{id}/upload",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity< EventAdminResponse > updateEventImage (
		@PathVariable Long id,
		@RequestParam ( "file" ) MultipartFile file,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventService.updateEventImage(id, file, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{id}" )
	public ResponseEntity< Void > deleteEvent (
		@PathVariable Long id,
		@AuthenticationPrincipal User user
	) {
		eventService.deleteEvent(id, user);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{id}/status" )
	public ResponseEntity< EventAdminResponse > updateEventStatus (
		@PathVariable Long id,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventService.toggleRegistrationStatus(id, user));
	}
}
