package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.constant.Position;
import com.megatronix.paridhi.dto.request.EventComboTeamRequest;
import com.megatronix.paridhi.dto.request.TeamRequest;
import com.megatronix.paridhi.dto.response.TeamAdminResponse;
import com.megatronix.paridhi.dto.response.TeamPublicResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ( "/api/teams" )
public class TeamController {
	private final TeamService teamService;

	// @PreAuthorize("isAuthenticated()")
	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ( "/register/event" )
	public ResponseEntity< TeamPublicResponse > registerTeam (
		@Valid @RequestBody TeamRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(teamService.registerTeam(request, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ( "/register/combo" )
	public ResponseEntity< List< TeamPublicResponse > > registerForCombo (
		@Valid @RequestBody EventComboTeamRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(teamService.registerTeamForEventCombo(request, user));
	}

	@PreAuthorize ( "isAuthenticated() and @teamSecurity.isTeamMember(#tid, authentication.name)" )
	@GetMapping ( "/{tid}" )
	public ResponseEntity< TeamPublicResponse > getTeamByTid (
		@PathVariable String tid,
		Principal principal
	) {
		return ResponseEntity.ok(teamService.getTeamByTid(tid, principal.getName()));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/{tid}/admin" )
	public ResponseEntity< TeamAdminResponse > getAdminTeamByTid (
		@PathVariable String tid,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getAdminTeamByTid(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{tid}/payment" )
	public ResponseEntity< TeamAdminResponse > updatePaymentStatus (
		@PathVariable String tid,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.updatePaymentStatus(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{tid}/played" )
	public ResponseEntity< TeamAdminResponse > updatePlayedStatus (
		@PathVariable String tid,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.updatePlayedStatus(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{tid}/qualified" )
	public ResponseEntity< TeamAdminResponse > updateQualifiedStatus (
		@PathVariable String tid,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.updateQualifiedStatus(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{tid}/position" )
	public ResponseEntity< TeamAdminResponse > updatePosition (
		@PathVariable String tid,
		@RequestParam ( name = "position" ) Position position,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.updatePosition(tid, position, user));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/events/{eventId}" )
	public ResponseEntity< List< TeamAdminResponse > > getTeamsByEvent (
		@PathVariable Long eventId,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getTeamsByEvent(eventId, user));
	}

	@PreAuthorize ( "isAuthenticated() and @teamSecurity.isGidOwner(#gid, authentication.name)" )
	@GetMapping ( "/gid/{gid}" )
	public ResponseEntity< List< TeamPublicResponse > > getTeamsByGid (
		@PathVariable String gid,
		Principal principal
	) {
		return ResponseEntity.ok(teamService.getTeamsByGid(gid, principal.getName()));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/admin/{gid}" )
	public ResponseEntity< List< TeamAdminResponse > > getTeamsByGid (
		@PathVariable String gid,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getAdminTeamsByGid(gid, user));
	}

	@PreAuthorize ( "isAuthenticated()" )
	@GetMapping ( "/me" )
	public ResponseEntity< List< TeamPublicResponse > > getTeamsByUserEmail (
		Principal principal
	) {
		return ResponseEntity.ok(teamService.getTeamsByUser(principal.getName()));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/admin/users/{email}" )
	public ResponseEntity< List< TeamAdminResponse > > getAdminTeamsByUserEmail (
		@PathVariable String email,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getAdminTeamsByUserEmail(email, user));
	}
}
