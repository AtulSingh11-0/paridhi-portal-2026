package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.dto.response.DomainPosterAdminResponse;
import com.megatronix.paridhi.dto.response.DomainPosterPublicResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.DomainPosterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping ( "/api/domain-posters" )
public class DomainPosterController {
	private final DomainPosterService domainPosterService;

	public DomainPosterController ( DomainPosterService domainPosterService ) {
		this.domainPosterService = domainPosterService;
	}

	// Public Endpoints

	@GetMapping ( "/{domain}" )
	public ResponseEntity< DomainPosterPublicResponse > getDomainPosterByName (
		@PathVariable Domain domain
	) {
		return ResponseEntity.ok(domainPosterService.getDomainPosterByDomain(domain));
	}

	@GetMapping
	public ResponseEntity< List< DomainPosterPublicResponse > > getAllDomainPosters () {
		return ResponseEntity.ok(domainPosterService.getAllDomainPosters());
	}

	// Authorized Endpoints

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PostMapping (
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity< DomainPosterAdminResponse > saveDomainPoster (
		@RequestParam ( "domain" ) Domain domain,
		@RequestParam ( "file" ) MultipartFile file,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(domainPosterService.saveDomainPoster(domain, file, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PutMapping (
		value = "/{id}",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity< DomainPosterAdminResponse > updateDomainPoster (
		@PathVariable Long id,
		@RequestParam ( "domain" ) Domain domain,
		@RequestParam ( "file" ) MultipartFile file,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(domainPosterService.updateDomainPoster(id, domain, file, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@DeleteMapping ( "/{id}" )
	public ResponseEntity< Void > deleteDomainPoster (
		@PathVariable Long id,
		@AuthenticationPrincipal User user
	) {
		domainPosterService.deleteDomainPoster(id, user);
		return ResponseEntity.noContent().build();
	}

}
