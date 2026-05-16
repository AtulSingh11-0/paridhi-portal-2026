package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.model.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema ( description = "Response payload for public team information" )
public record TeamPublicResponse(

	@Schema ( description = "Unique identifier of the team", example = "1" )
	Long id,

	@Schema ( description = "Name of the team", example = "The Hackers" )
	String teamName,

	@Schema ( description = "Unique team ID (TID)", example = "TD26-001-AC" )
	String tid,

	@Schema ( description = "ID of the event the team is registered for", example = "4" )
	Long eventId,

	@Schema ( description = "Name of the event the team is registered for", example = "Code-O-Fiesta" )
	String eventName,

	@Schema ( description = "List of contact persons for the team" )
	List< ContactDto > contacts,

	@Schema (
		description = "List of GIDs of the team members",
		example = """
		["PD26-0001-AC", "PD26-0002-AC"]
		"""
	)
	List< String > gidList,

	@Schema ( description = "Whether the team has paid the registration fee", example = "true" )
	boolean hasPaid,

	@Schema ( description = "Whether the team has played in the event", example = "false" )
	boolean hasPlayed,

	@Schema ( description = "Whether the team has qualified for the next round", example = "false" )
	boolean hasQualified
) {
	public static TeamPublicResponse fromTeam ( Team team ) {
		if ( team == null ) return null;

		var event = team.getEvent();

		return TeamPublicResponse.builder()
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
			.build();
	}
}
