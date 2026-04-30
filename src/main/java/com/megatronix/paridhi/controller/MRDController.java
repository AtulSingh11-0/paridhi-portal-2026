package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.request.AdminMRDRequest;
import com.megatronix.paridhi.dto.response.MRDResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.MRDService;
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
public class MRDController {
	private final MRDService mrdService;

	// Public Endpoints

	//  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
	@PostMapping ( "/register" )
	public ResponseEntity< MRDResponse > registerMRD (
		@RequestHeader ( name = "Authorization" ) String authHeader
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(mrdService.performUserMRD(authHeader));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping ( "/admin/register" )
	public ResponseEntity< MRDResponse > registerMRDByAdmin (
		@Valid @RequestBody AdminMRDRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(mrdService.performUserMRDByAdmin(request));
	}


	// Authorized endpoints

	@PreAuthorize ( "hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/user/{email}" )
	public ResponseEntity< List< MRDResponse > > getUserMRDs (
		@PathVariable String email,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.getUserMRDs(email, user));
	}

	@PreAuthorize ( "hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/user/{email}/gids" )
	public ResponseEntity< List< String > > getUserGids (
		@PathVariable String email,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.getUserGids(email, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/{gid}" )
	public ResponseEntity< MRDResponse > getMRDbyGID (
		@PathVariable String gid,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.getMRDbyGID(gid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "{gid}/payment" )
	public ResponseEntity< MRDResponse > updatePaymentStatus (
		@PathVariable String gid,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.updatePaymentStatus(gid, user));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping
	public ResponseEntity< List< MRDResponse > > getAllMRDs (
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(mrdService.getAllMRDs(user));
	}
}
