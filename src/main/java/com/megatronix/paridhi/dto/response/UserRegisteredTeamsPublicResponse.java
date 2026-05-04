package com.megatronix.paridhi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@Schema(description = "Response payload grouping a user's teams by their GID")
@JsonInclude ( NON_NULL )
public record UserRegisteredTeamsPublicResponse(

	@Schema(description = "The General ID (GID) associated with the user", example = "PD26-1234-AB")
	String gid,

	@Schema(description = "List of teams the user is part of under this GID")
	@JsonInclude ( NON_EMPTY )
	List< TeamPublicResponse > teams
) {
	public static UserRegisteredTeamsPublicResponse of ( String gid, List< TeamPublicResponse > teams ) {
		return UserRegisteredTeamsPublicResponse.builder()
			.gid(gid)
			.teams(teams)
			.build();
	}
}
