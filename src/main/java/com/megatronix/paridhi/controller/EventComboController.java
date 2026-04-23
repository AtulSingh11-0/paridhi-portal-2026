package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.dto.request.EventComboRequest;
import com.megatronix.paridhi.dto.response.EventComboAdminResponse;
import com.megatronix.paridhi.dto.response.EventComboPublicResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.EventComboService;
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
public class EventComboController {
	private final EventComboService eventComboService;

	public EventComboController (
		EventComboService eventComboService
	) {
		this.eventComboService = eventComboService;
	}

	// Public endpoints

	@GetMapping
	public ResponseEntity< List< EventComboPublicResponse > > getEventCombos () {
		return ResponseEntity.ok(eventComboService.getAllEventCombos());
	}

	@GetMapping ( "/{eventComboId}" )
	public ResponseEntity< EventComboPublicResponse > getEventComboById (
		@PathVariable ( name = "eventComboId" ) Long id
	) {
		return ResponseEntity.ok(eventComboService.getEventComboById(id));
	}

	@GetMapping ( "/domains/{domain}" )
	public ResponseEntity< List< EventComboPublicResponse > > getEventCombosByDomain (
		@PathVariable Domain domain
	) {
		return ResponseEntity.ok(eventComboService.getEventCombosByDomain(domain));
	}

	@GetMapping ( "/status" )
	public ResponseEntity< List< EventComboPublicResponse > > getEventCombosByStatus (
		@RequestParam ( name = "isOpen" ) boolean isOpen
	) {
		return ResponseEntity.ok(eventComboService.getEventCombosByStatus(isOpen));
	}

	// Authorized endpoints

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping
	public ResponseEntity< EventComboAdminResponse > createEventCombo (
		@Valid @RequestBody EventComboRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(eventComboService.createEventCombo(request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping ( "/{eventComboId}" )
	public ResponseEntity< EventComboAdminResponse > updateEventCombo (
		@PathVariable ( name = "eventComboId" ) Long id,
		@Valid @RequestBody EventComboRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.updateEventCombo(id, request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{eventComboId}" )
	public ResponseEntity< Void > deleteEventCombo (
		@PathVariable ( name = "eventComboId" ) Long id,
		@AuthenticationPrincipal User user
	) {
		eventComboService.deleteEventCombo(id, user);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{eventComboId}/status" )
	public ResponseEntity< EventComboAdminResponse > toggleEventComboRegistrationStatus (
		@PathVariable ( name = "eventComboId" ) Long id,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.toggleEventComboRegistrationStatus(id, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping (
		value = "/{eventComboId}/upload",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity< EventComboAdminResponse > updateEventComboImage (
		@PathVariable ( name = "eventComboId" ) Long id,
		@RequestParam ( "file" ) MultipartFile file,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.updateEventComboImage(id, file, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ( "/{eventComboId}/events/{eventId}" )
	public ResponseEntity< EventComboAdminResponse > addSingleEventToEventCombo (
		@PathVariable Long eventComboId,
		@PathVariable Long eventId,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.addSingleEventToEventCombo(eventComboId, eventId, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{eventComboId}/events/{eventId}" )
	public ResponseEntity< EventComboAdminResponse > removeSingleEventFromEventCombo (
		@PathVariable Long eventComboId,
		@PathVariable Long eventId,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(eventComboService.removeSingleEventFromEventCombo(eventComboId, eventId, user));
	}
}
