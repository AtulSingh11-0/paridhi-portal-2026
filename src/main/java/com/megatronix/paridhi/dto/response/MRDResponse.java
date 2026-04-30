package com.megatronix.paridhi.dto.response;

import lombok.Builder;

@Builder
public record MRDResponse(
	String gid
) {
	public static MRDResponse from ( String gid ) {
		return MRDResponse.builder()
			.gid(gid)
			.build();
	}
}