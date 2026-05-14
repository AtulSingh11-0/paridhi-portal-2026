package com.megatronix.paridhi.controller;

import com.megatronix.paridhi.constant.Position;
import com.megatronix.paridhi.dto.request.EventComboTeamRequest;
import com.megatronix.paridhi.dto.request.TeamRequest;
import com.megatronix.paridhi.dto.response.*;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.service.TeamService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ( "/api/teams" )
@Tag ( name = "Team", description = "Endpoints for managing teams, event registrations, combo registrations, and retrieving team details." )
public class TeamController {

	private final TeamService teamService;

	@PreAuthorize ( "isAuthenticated()" )
	@PostMapping ( "/register/event" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Register team for a single event", description = "Allows an authenticated user to register a team for a specific event by providing team details and GIDs." )
	@io.swagger.v3.oas.annotations.parameters.RequestBody ( content = @Content ( schema = @Schema ( implementation = TeamRequest.class ), examples = @ExampleObject ( name = "Register Event", value = """
		{
		  "teamName": "The Hackers",
		  "eventId": 4,
		  "gidList": ["PD-8374-AZ", "PD-1923-AZ"],
		  "contacts": [
		    { "name": "John Doe", "phoneNumber": "9876543210" }
		  ]
		}""" ) ) )
	@ApiResponse ( responseCode = "201", description = "Team successfully registered", content = @Content ( schema = @Schema ( implementation = TeamPublicResponse.class ), examples = @ExampleObject ( name = "Team Registered", value = """
		{
		  "id": 1,
		  "teamName": "The Hackers",
		  "tid": "TD26-582-AE",
		  "eventId": 4,
		  "eventName": "Code-O-Fiesta",
		  "contacts": [
		    { "name": "John Doe", "phoneNumber": "9876543210" }
		  ],
		  "gidList": ["PD-8374-AZ", "PD-1923-AZ"],
		  "hasPaid": false,
		  "hasPlayed": false,
		  "hasQualified": false
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation failed", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< TeamPublicResponse > registerTeam (
		@Valid @RequestBody TeamRequest request,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(teamService.registerTeam(request, user));
	}

	@PreAuthorize ( "isAuthenticated()" )
	@PostMapping ( "/register/combo" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Register team for an event combo", description = "Allows an authenticated user to register a team for all events within a combo by providing a mapping of event IDs to GIDs." )
	@io.swagger.v3.oas.annotations.parameters.RequestBody ( content = @Content ( schema = @Schema ( implementation = EventComboTeamRequest.class ), examples = @ExampleObject ( name = "Register Combo", value = """
		{
		  "comboId": 1,
		  "teamName": "The Hackers",
		  "eventGidMap": {
		    "4": ["PD-8374-AZ", "PD-1923-AZ"],
		    "5": ["PD-8374-AZ"]
		  },
		  "contacts": [
		    { "name": "John Doe", "phoneNumber": "9876543210" }
		  ]
		}""" ) ) )
	@ApiResponse ( responseCode = "201", description = "Teams successfully registered for the combo", content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = TeamPublicResponse.class ) ), examples = @ExampleObject ( name = "Combo Teams Registered", value = """
		[
		  {
		    "id": 1,
		    "teamName": "The Hackers",
		    "tid": "TD26-582-AE",
		    "eventId": 4,
		    "eventName": "Code-O-Fiesta",
		    "contacts": [ { "name": "John Doe", "phoneNumber": "9876543210" } ],
		    "gidList": ["PD-8374-AZ", "PD-1923-AZ"],
		    "hasPaid": false,
		    "hasPlayed": false,
		    "hasQualified": false
		  },
		  {
		    "id": 2,
		    "teamName": "The Hackers",
		    "tid": "TD26-401-AE",
		    "eventId": 5,
		    "eventName": "Hackathon",
		    "contacts": [ { "name": "John Doe", "phoneNumber": "9876543210" } ],
		    "gidList": ["PD-8374-AZ"],
		    "hasPaid": false,
		    "hasPlayed": false,
		    "hasQualified": false
		  }
		]""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Validation failed", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< TeamPublicResponse > > registerForCombo (
		@Valid @RequestBody EventComboTeamRequest request,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(teamService.registerTeamForEventCombo(request, user));
	}

	@PreAuthorize ( "isAuthenticated() and @teamSecurity.isTeamMember(#tid, authentication.name)" )
	@GetMapping ( "/{tid}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Get team by TID", description = "Retrieves public team details for a specific Team ID (TID). Requires the authenticated user to be a member of the team." )
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved team details", content = @Content ( schema = @Schema ( implementation = TeamPublicResponse.class ), examples = @ExampleObject ( name = "Team Details", value = """
		{
		  "id": 1,
		  "teamName": "The Hackers",
		  "tid": "TD26-582-AE",
		  "eventId": 4,
		  "eventName": "Code-O-Fiesta",
		  "contacts": [ { "name": "John Doe", "phoneNumber": "9876543210" } ],
		  "gidList": ["PD-8374-AZ", "PD-1923-AZ"],
		  "hasPaid": false,
		  "hasPlayed": false,
		  "hasQualified": false
		}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Team not found", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	@ApiResponse ( responseCode = "403", description = "Unauthorized access to team", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< TeamPublicResponse > getTeamByTid (
		@Parameter ( description = "Team ID", example = "TD26-582-AE" ) @PathVariable String tid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getTeamByTid(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@GetMapping ( "/{tid}/admin" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Get team by TID (Admin)", description = "Retrieves detailed admin team information for a specific Team ID (TID). Restricted to Admin and Super Admin users." )
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved admin team details", content = @Content ( schema = @Schema ( implementation = TeamAdminResponse.class ), examples = @ExampleObject ( name = "Admin Team Details", value = """
		{
		  "id": 1,
		  "teamName": "The Hackers",
		  "tid": "TD26-582-AE",
		  "eventId": 4,
		  "eventName": "Code-O-Fiesta",
		  "contacts": [ { "name": "John Doe", "phoneNumber": "9876543210" } ],
		  "gidList": ["PD-8374-AZ", "PD-1923-AZ"],
		  "hasPaid": false,
		  "hasPlayed": false,
		  "hasQualified": false,
		  "position": "NONE",
		  "createdAt": 1778839200.000000000,
		  "updatedAt": 1778839200.000000000,
		  "createdByUsername": "john@doe.com",
		  "updatedByUsername": "john@doe.com"
		}""" ) ) )
	@ApiResponse ( responseCode = "404", description = "Team not found", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< TeamAdminResponse > getAdminTeamByTid (
		@Parameter ( description = "Team ID", example = "TD26-582-AE" ) @PathVariable String tid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getAdminTeamByTid(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{tid}/payment" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Update team payment status", description = "Toggles the payment status of a team. Restricted to Admin and Super Admin users." )
	@ApiResponse ( responseCode = "200", description = "Payment status updated", content = @Content ( schema = @Schema ( implementation = TeamAdminResponse.class ), examples = @ExampleObject ( name = "Payment Status Updated", value = """
		{
		  "id": 1,
		  "tid": "TD26-582-AE",
		  "hasPaid": true,
		  "updatedAt": 1778839200.000000000
		}""" ) ) )
	public ResponseEntity< TeamAdminResponse > updatePaymentStatus (
		@Parameter ( description = "Team ID", example = "TD26-582-AE" ) @PathVariable String tid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.updatePaymentStatus(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{tid}/played" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Update team played status", description = "Toggles the played status of a team. Requires the team to have paid. Restricted to Admin and Super Admin users." )
	@ApiResponse ( responseCode = "200", description = "Played status updated", content = @Content ( schema = @Schema ( implementation = TeamAdminResponse.class ), examples = @ExampleObject ( name = "Played Status Updated", value = """
		{
		  "id": 1,
		  "tid": "TD26-582-AE",
		  "hasPlayed": true,
		  "updatedAt": 1778839200.000000000
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Team has not paid", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< TeamAdminResponse > updatePlayedStatus (
		@Parameter ( description = "Team ID", example = "TD26-582-AE" ) @PathVariable String tid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.updatePlayedStatus(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{tid}/qualified" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Update team qualified status", description = "Toggles the qualified status of a team. Requires the team to have played. Restricted to Admin and Super Admin users." )
	@ApiResponse ( responseCode = "200", description = "Qualified status updated", content = @Content ( schema = @Schema ( implementation = TeamAdminResponse.class ), examples = @ExampleObject ( name = "Qualified Status Updated", value = """
		{
		  "id": 1,
		  "tid": "TD26-582-AE",
		  "hasQualified": true,
		  "updatedAt": 1778839200.000000000
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Team has not played", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< TeamAdminResponse > updateQualifiedStatus (
		@Parameter ( description = "Team ID", example = "TD26-582-AE" ) @PathVariable String tid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.updateQualifiedStatus(tid, user));
	}

	@PreAuthorize ( "hasAnyRole('ADMIN', 'SUPERADMIN')" )
	@PatchMapping ( "/{tid}/position" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Update team position", description = "Updates the winning position of a team. Requires the team to be qualified. Restricted to Admin and Super Admin users." )
	@ApiResponse ( responseCode = "200", description = "Position updated", content = @Content ( schema = @Schema ( implementation = TeamAdminResponse.class ), examples = @ExampleObject ( name = "Position Updated", value = """
		{
		  "id": 1,
		  "tid": "TD26-582-AE",
		  "position": "FIRST",
		  "updatedAt": 1778839200.000000000
		}""" ) ) )
	@ApiResponse ( responseCode = "400", description = "Team is not qualified", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< TeamAdminResponse > updatePosition (
		@Parameter ( description = "Team ID", example = "TD26-582-AE" ) @PathVariable String tid,
		@Parameter ( description = "Winning position", example = "FIRST" ) @RequestParam ( name = "position" ) Position position,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.updatePosition(tid, position, user));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/events/{eventId}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Get teams by Event ID", description = "Retrieves all teams registered for a specific event. Restricted to Super Admin only." )
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved teams", content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = TeamAdminResponse.class ) ), examples = @ExampleObject ( name = "Event Teams", value = """
		[
		  {
		    "id": 1,
		    "tid": "TD26-582-AE",
		    "eventId": 4
		  }
		]""" ) ) )
	public ResponseEntity< List< TeamAdminResponse > > getTeamsByEvent (
		@Parameter ( description = "Event ID", example = "4" ) @PathVariable Long eventId,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getTeamsByEvent(eventId, user));
	}

	@PreAuthorize ( "isAuthenticated() and @teamSecurity.isOwnerOfGid(#gid, authentication.name)" )
	@GetMapping ( "/gid/{gid}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Get teams by GID", description = "Retrieves all teams associated with a specific GID. Requires the authenticated user to own the GID." )
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved teams", content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = TeamPublicResponse.class ) ), examples = @ExampleObject ( name = "Teams by GID", value = """
		[
		  {
		    "id": 1,
		    "tid": "TD26-582-AE",
		    "gidList": ["PD-8374-AZ"]
		  }
		]""" ) ) )
	@ApiResponse ( responseCode = "403", description = "Unauthorized access to GID", content = @Content ( schema = @Schema ( implementation = ErrorResponse.class ) ) )
	public ResponseEntity< List< TeamPublicResponse > > getTeamsByGid (
		@Parameter ( description = "General ID", example = "PD-8374-AZ" ) @PathVariable String gid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getTeamsByGid(gid, user));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/admin/{gid}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Get admin teams by GID", description = "Retrieves all detailed team admin information associated with a specific GID. Restricted to Super Admin only." )
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved admin teams", content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = TeamAdminResponse.class ) ) ) )
	public ResponseEntity< List< TeamAdminResponse > > getTeamsByGidAdmin (
		@Parameter ( description = "General ID", example = "PD-8374-AZ" ) @PathVariable String gid,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getAdminTeamsByGid(gid, user));
	}

	@PreAuthorize ( "isAuthenticated()" )
	@GetMapping ( "/me" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Get teams for current user", description = "Retrieves all teams associated with any of the current authenticated user's GIDs." )
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved user's teams grouped by GID", content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = UserRegisteredTeamsPublicResponse.class ) ), examples = @ExampleObject ( name = "User Teams", value = """
		[
		  {
		    "gid": "PD26-1234-AB",
		    "teams": [
		      {
		        "id": 1,
		        "teamName": "The Hackers",
		        "tid": "TD26-582-AE",
		        "eventId": 4,
		        "eventName": "Code-O-Fiesta",
		        "contacts": [ { "name": "John Doe", "phoneNumber": "9876543210" } ],
		        "gidList": ["PD26-1234-AB", "PD26-1923-AZ"],
		        "hasPaid": false,
		        "hasPlayed": false,
		        "hasQualified": false
		      }
		    ]
		  }
		]""" ) ) )
	public ResponseEntity< List< UserRegisteredTeamsPublicResponse > > getTeamsByUserEmail (
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getTeamsByUser(user));
	}

	@PreAuthorize ( "hasRole('SUPERADMIN')" )
	@GetMapping ( "/admin/users/{email}" )
	@SecurityRequirement ( name = "Bearer Authentication" )
	@Operation ( summary = "Get admin teams by user email", description = "Retrieves all detailed team admin information associated with a specific user email. Restricted to Super Admin only." )
	@ApiResponse ( responseCode = "200", description = "Successfully retrieved user's admin teams grouped by GID", content = @Content ( array = @ArraySchema ( schema = @Schema ( implementation = UserRegisteredTeamsAdminResponse.class ) ), examples = @ExampleObject ( name = "Admin User Teams", value = """
		[
		  {
		    "gid": "PD26-1234-AB",
		    "teams": [
		      {
		        "id": 1,
		        "teamName": "The Hackers",
		        "tid": "TD26-582-AE",
		        "eventId": 4,
		        "eventName": "Code-O-Fiesta",
		        "contacts": [ { "name": "John Doe", "phoneNumber": "9876543210" } ],
		        "gidList": ["PD26-1234-AB", "PD26-1923-AZ"],
		        "hasPaid": false,
		        "hasPlayed": false,
		        "hasQualified": false,
		        "position": "NONE",
		        "createdAt": 1778839200.000000000,
		        "updatedAt": 1778839200.000000000,
		        "createdByUsername": "john@doe.com",
		        "updatedByUsername": "john@doe.com"
		      }
		    ]
		  }
		]""" ) ) )
	public ResponseEntity< List< UserRegisteredTeamsAdminResponse > > getAdminTeamsByUserEmail (
		@Parameter ( description = "User's email", example = "user@example.com" ) @PathVariable String email,
		@Parameter ( hidden = true ) @AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(teamService.getAdminTeamsByUserEmail(email, user));
	}
}
