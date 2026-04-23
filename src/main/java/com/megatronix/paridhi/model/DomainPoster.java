package com.megatronix.paridhi.model;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.Domain;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table ( name = "domain_posters" )
public class DomainPoster extends Auditable {
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	@Column ( name = "domain_poster_id", updatable = false, nullable = false )
	private Long domainPosterId;

	@Enumerated ( EnumType.STRING )
	@Column ( name = "domain", nullable = false, unique = true )
	private Domain domain;

	@Column ( name = "domain_poster_url", nullable = false )
	@Builder.Default
	private String domainPosterUrl = AppConstant.DEFAULT_EVENT_POSTER_URL;

	@Column ( name = "domain_poster_file_id", nullable = false )
	@Builder.Default
	private String domainPosterFileId = AppConstant.DEFAULT_EVENT_POSTER_FILE_ID;
}
