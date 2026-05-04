package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@Schema(description = "Response payload grouping a user's teams by their GID (Admin view)")
@JsonInclude ( NON_NULL )
public record UserRegisteredTeamsAdminResponse(

	@Schema(description = "The General ID (GID) associated with the user", example = "PD26-1234-AB")
	String gid,

	@Schema(description = "List of teams the user is part of under this GID with admin details")
	@JsonInclude ( NON_EMPTY )
	List< TeamAdminResponse > teams
) {
	public static UserRegisteredTeamsAdminResponse of ( String gid, List< TeamAdminResponse > teams ) {
		return UserRegisteredTeamsAdminResponse.builder()
			.gid(gid)
			.teams(teams)
			.build();
	}
}
