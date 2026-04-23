package com.megatronix.paridhi.model;

import com.megatronix.paridhi.util.RandomUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table ( name = "password_reset_tokens" )
public class PasswordResetToken extends Auditable {
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	@Column ( name = "token_id", updatable = false, nullable = false )
	private Long tokenId;

	@Column ( unique = true, updatable = false, nullable = false, length = 6 )
	private String token;

	@ManyToOne ( fetch = FetchType.LAZY )
	@JoinColumn ( nullable = false, name = "user_id" )
	private User user;

	@Column ( name = "expiry_date", nullable = false )
	private Instant expiryDate;

	@Builder.Default
	@Column ( name = "is_used", nullable = false )
	private boolean isUsed = false;

	@Builder.Default
	@Column( name = "attempts", nullable = false )
	private int attempts = 0;

	public static final int MAX_ATTEMPTS = 5;

	@PrePersist
	protected void onCreate () {
		if ( this.token == null || this.token.trim().isEmpty() ) {
			this.token = RandomUtil.generateResetToken(6);
		}
		// token will be valid for 10 minutes
		this.expiryDate = Instant.now().plus(10, ChronoUnit.MINUTES);
	}

	public boolean isExpired () {
		return Instant.now().isAfter(this.expiryDate);
	}

	public void incrementAttempts () {
		this.attempts++;
		if ( this.attempts >= MAX_ATTEMPTS ) {
			this.isUsed = true; // disable the token after max attempts
		}
	}

	public boolean isLocked () {
		return this.attempts >= MAX_ATTEMPTS;
	}
}
