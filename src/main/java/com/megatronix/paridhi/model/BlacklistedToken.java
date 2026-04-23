package com.megatronix.paridhi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table ( name = "blacklisted_tokens" )
public class BlacklistedToken extends Auditable {
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	@Column ( name = "token_id", updatable = false, nullable = false )
	private Long tokenId;

	@Column ( unique = true, nullable = false, updatable = false, length = 1000 )
	private String token;

	@Column ( name = "expires_at", nullable = false, updatable = false )
	private Instant expiresAt;

	public boolean isExpired () {
		return Instant.now().isAfter(expiresAt);
	}
}
