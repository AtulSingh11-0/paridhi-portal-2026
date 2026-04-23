package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.model.Team;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamPublicResponse(
	Long id,
	String teamName,
	String tid,
	Long eventId,
	String eventName,
	List< ContactDto > contacts,
	List< String > gidList,
	boolean hasPaid,
	boolean hasPlayed,
	boolean hasQualified
) {
	public static TeamPublicResponse fromTeam ( Team team ) {
		return TeamPublicResponse.builder()
			.id(team.getTeamId())
			.teamName(team.getTeamName())
			.tid(team.getTid())
			.eventId(team.getEvent().getEventId())
			.eventName(team.getEvent().getEventName())
			.contacts(
				team.getContacts().stream()
					.map(ContactDto::fromContact)
					.toList()
			)
			.gidList(team.getGidList())
			.hasPaid(team.isHasPaid())
			.hasPlayed(team.isHasPlayed())
			.hasQualified(team.isHasQualified())
			.build();
	}
}
