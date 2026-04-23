package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.DomainPoster;
import lombok.Builder;

import java.time.Instant;

@Builder
public record DomainPosterAdminResponse(
	Long id,
	Domain domain,
	String posterUrl,
	String posterFileId,
	Instant createdAt,
	Instant updatedAt,
	String createdByUsername,
	String updatedByUsername
) {
	public static DomainPosterAdminResponse fromDomainPoster ( DomainPoster domainPoster ) {
		return DomainPosterAdminResponse.builder()
			.id(domainPoster.getDomainPosterId())
			.domain(domainPoster.getDomain())
			.posterUrl(domainPoster.getDomainPosterUrl())
			.posterFileId(domainPoster.getDomainPosterFileId())
			.createdAt(domainPoster.getCreatedAt())
			.updatedAt(domainPoster.getUpdatedAt())
			.createdByUsername(domainPoster.getCreatedBy())
			.updatedByUsername(domainPoster.getUpdatedBy())
			.build();
	}
}
