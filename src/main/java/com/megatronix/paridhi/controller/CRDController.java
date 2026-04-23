package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.response.TeamAdminResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.CRDService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ( "/api/crd/events" )
public class CRDController {
	private final CRDService crdService;

	// Authorized endpoints

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/{eventId}/prelims" )
	public ResponseEntity< List< TeamAdminResponse > > getTeamsForPrelims (
		@PathVariable Long eventId,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(crdService.getTeamsByEventForPrelims(eventId, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/{eventId}/finals" )
	public ResponseEntity< List< TeamAdminResponse > > getTeamsForFinals (
		@PathVariable Long eventId,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(crdService.getTeamsByEventForFinals(eventId, user));
	}
}