package com.megatronix.paridhi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema ( description = "Response payload containing the generated GID for an MRD registration" )
public record MRDResponse(
	@Schema ( description = "The generated Paridhi General ID", example = "PD26-0001-AC" )
	String gid
) {
	public static MRDResponse from ( String gid ) {
		return MRDResponse.builder()
			.gid(gid)
			.build();
	}
}