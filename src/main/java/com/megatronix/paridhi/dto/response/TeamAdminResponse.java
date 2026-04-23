package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.constant.Position;
import com.megatronix.paridhi.model.Team;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record TeamAdminResponse(
	Long id,
	String teamName,
	String tid,
	Long eventId,
	String eventName,
	List< ContactDto > contacts,
	List< String > gidList,
	boolean hasPaid,
	boolean hasPlayed,
	boolean hasQualified,
	Position position,
	Instant createdAt,
	Instant updatedAt,
	String createdByUsername,
	String updatedByUsername
) {
	public static TeamAdminResponse fromTeam ( Team team ) {
		return TeamAdminResponse.builder()
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
			.position(team.getPosition())
			.createdAt(team.getCreatedAt())
			.updatedAt(team.getUpdatedAt())
			.createdByUsername(team.getCreatedBy())
			.updatedByUsername(team.getUpdatedBy())
			.build();
	}
}
