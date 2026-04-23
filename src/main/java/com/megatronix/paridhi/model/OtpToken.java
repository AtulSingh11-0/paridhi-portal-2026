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
@Table ( name = "otp_tokens" )
public class OtpToken extends Auditable {
	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	@Column ( name = "otp_id", nullable = false, updatable = false )
	private Long otpId;

	@Column ( nullable = false, length = 6, updatable = false )
	private String otp;

	@Column ( nullable = false, updatable = false )
	private String email;

	@ManyToOne ( fetch = FetchType.LAZY )
	@JoinColumn ( nullable = false, name = "user_id")
	private User user;

	@Column ( name = "expires_at", nullable = false )
	private Instant expiryDate;

	@Builder.Default
	@Column( name = "is_used", nullable = false )
	private boolean isUsed = false;

	@Builder.Default
	@Column( name = "attempts", nullable = false )
	private int attempts = 0;

	public static final int MAX_ATTEMPTS = 5;

	@PrePersist
	protected void onCreate () {
		if ( this.otp == null || this.otp.trim().isEmpty() ) {
			this.otp = RandomUtil.generateOtp();
		}
		// OTP valid for 10 minutes
		this.expiryDate = Instant.now().plus(10, ChronoUnit.MINUTES);

		// if email is null, set it from the user object
		if ( this.email == null && this.user != null ) {
			this.email = this.user.getEmail();
		}
	}

	public boolean isExpired () {
		return Instant.now().isAfter(expiryDate);
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
