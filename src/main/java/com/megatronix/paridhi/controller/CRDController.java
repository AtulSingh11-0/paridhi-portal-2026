package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.dto.response.ErrorResponse;
import com.megatronix.paridhi.dto.response.TeamAdminResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.CRDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping ( "/api/crd/events" )
@Tag ( name = "CRD", description = "Endpoints for managing CRD operations and fetching qualified teams for events." )
public class CRDController {

	private final CRDService crdService;

	public CRDController ( CRDService crdService ) {
		this.crdService = crdService;
	}

	// Authorized endpoints

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/{eventId}/prelims" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get teams for Event Prelims",
		description = "Retrieves all teams eligible for the preliminary round of a specific event. Requires all members to have paid MRD fees, the team to have paid the event fee, and the team not being qualified yet. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved teams for prelims",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = TeamAdminResponse.class ) ),
			examples = @ExampleObject ( name = "Prelims Teams", value = """
				[
				  {
				    "id": 1,
				    "teamName": "The Hackers",
				    "tid": "TD26-582-AE",
				    "eventId": 4,
				    "eventName": "Code-O-Fiesta",
				    "contacts": [ { "name": "John Doe", "phoneNumber": "9876543210" } ],
				    "gidList": ["PD-8374-AZ", "PD-1923-AZ"],
				    "hasPaid": true,
				    "hasPlayed": false,
				    "hasQualified": false,
				    "position": "NONE",
				    "createdAt": 1778839200.000000000,
				    "updatedAt": 1778839200.000000000,
				    "createdByUsername": "john@doe.com",
				    "updatedByUsername": "john@doe.com"
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< TeamAdminResponse > > getTeamsForPrelims (
		@Parameter ( description = "Event ID", example = "4" ) @PathVariable Long eventId,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(crdService.getTeamsByEventForPrelims(eventId, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/{eventId}/finals" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation (
		summary = "Get teams for Event Finals",
		description = "Retrieves all teams eligible for the final round of a specific event. Requires the team to have paid, played, and qualified. Restricted to Admin and Super Admin users."
	)
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved teams for finals",
		content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = TeamAdminResponse.class ) ),
			examples = @ExampleObject ( name = "Finals Teams", value = """
				[
				  {
				    "id": 1,
				    "teamName": "The Hackers",
				    "tid": "TD26-582-AE",
				    "eventId": 4,
				    "eventName": "Code-O-Fiesta",
				    "contacts": [ { "name": "John Doe", "phoneNumber": "9876543210" } ],
				    "gidList": ["PD-8374-AZ", "PD-1923-AZ"],
				    "hasPaid": true,
				    "hasPlayed": true,
				    "hasQualified": true,
				    "position": "NONE",
				    "createdAt": 1778839200.000000000,
				    "updatedAt": 1778839200.000000000,
				    "createdByUsername": "john@doe.com",
				    "updatedByUsername": "john@doe.com"
				  }
				]""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Event not found",
		content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< TeamAdminResponse > > getTeamsForFinals (
		@Parameter ( description = "Event ID", example = "4" ) @PathVariable Long eventId,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(crdService.getTeamsByEventForFinals(eventId, user));
	}
}