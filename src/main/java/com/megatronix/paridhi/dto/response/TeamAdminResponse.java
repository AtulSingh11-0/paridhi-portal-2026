package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.constant.Position;
import com.megatronix.paridhi.model.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@Schema(description = "Response payload for detailed team information (Admin view)")
public record TeamAdminResponse(
		@Schema(description = "Unique identifier of the team", example = "1") Long id,
		@Schema(description = "Name of the team", example = "The Hackers") String teamName,
		@Schema(description = "Unique team ID (TID)", example = "TD26-001-AQ") String tid,
		@Schema(description = "ID of the event the team is registered for", example = "4") Long eventId,
		@Schema(description = "Name of the event the team is registered for", example = "Code-O-Fiesta") String eventName,
		@Schema(description = "List of contact persons for the team") List<ContactDto> contacts,
		@Schema(description = "List of GIDs of the team members", example = "[\"PD26-0001-AE\", \"PD26-0002-AE\"]") List<String> gidList,
		@Schema(description = "Whether the team has paid the registration fee", example = "true") boolean hasPaid,
		@Schema(description = "Whether the team has played in the event", example = "false") boolean hasPlayed,
		@Schema(description = "Whether the team has qualified for the next round", example = "false") boolean hasQualified,
		@Schema(description = "Position of the team in the event", example = "FIRST") Position position,
		@Schema(description = "Timestamp when the team was created", example = "1778839200.000000000") Instant createdAt,
		@Schema(description = "Timestamp when the team was last updated", example = "1778839200.000000000") Instant updatedAt,
		@Schema(description = "Username of the admin who created the team", example = "john@doe.com") String createdByUsername,
		@Schema(description = "Username of the admin who last updated the team", example = "john@doe.com") String updatedByUsername) {
	public static TeamAdminResponse fromTeam(Team team) {
		if (team == null) return null;

		var event = team.getEvent();
		
		return TeamAdminResponse.builder()
				.id(team.getTeamId())
				.teamName(team.getTeamName())
				.tid(team.getTid())
				.eventId(event != null ? event.getEventId() : null)
				.eventName(event != null ? event.getEventName() : "Unknown Event")
				.contacts(team.getContacts() != null ? 
						team.getContacts().stream()
								.map(ContactDto::fromContact)
								.toList() : java.util.Collections.emptyList())
				.gidList(team.getGidList() != null ? 
						new java.util.ArrayList<>(team.getGidList()) : java.util.Collections.emptyList())
				.hasPaid(team.isHasPaid())
				.hasPlayed(team.isHasPlayed())
				.hasQualified(team.isHasQualified())
				.position(team.getPosition())
				.createdAt(team.getCreatedAt())
				.updatedAt(team.getUpdatedAt())
				.createdByUsername(team.getCreatedBy())
				.updatedByUsername(team.getUpdatedBy())
				.build();
	}
}
