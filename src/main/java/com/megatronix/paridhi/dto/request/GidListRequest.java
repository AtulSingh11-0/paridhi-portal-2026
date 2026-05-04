package com.megatronix.paridhi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record GidListRequest(

	@Valid
	@NotEmpty ( message = "GID list cannot be empty" )
	@Schema (
		description = "List of GIDs to fetch details for",
		example = """
			["PD26-0001-AD", "PD26-0002-AD"]
			"""
	)
	List< String > gids
) {
}
