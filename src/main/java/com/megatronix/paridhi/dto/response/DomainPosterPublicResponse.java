package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.DomainPoster;
import lombok.Builder;

@Builder
public record DomainPosterPublicResponse(
	Long id,
	Domain domain,
	String posterUrl
) {
	public static DomainPosterPublicResponse fromDomainPoster ( DomainPoster domainPoster ) {
		return DomainPosterPublicResponse.builder()
			.id(domainPoster.getDomainPosterId())
			.domain(domainPoster.getDomain())
			.posterUrl(domainPoster.getDomainPosterUrl())
			.build();
	}
}
