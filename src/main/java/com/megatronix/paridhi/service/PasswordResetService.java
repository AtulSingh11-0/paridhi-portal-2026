package com.megatronix.paridhi.service;

import com.megatronix.paridhi.dto.request.PasswordResetConfirmationRequest;
import com.megatronix.paridhi.dto.request.PasswordResetRequest;
import com.megatronix.paridhi.dto.response.MessageResponse;
import com.megatronix.paridhi.dto.response.TokenValidationResponse;
import com.megatronix.paridhi.exception.InvalidResetTokenException;
import com.megatronix.paridhi.exception.TokenExpiredException;
import com.megatronix.paridhi.model.PasswordResetToken;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.PasswordResetTokenRepository;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.util.TokenSecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetService {
	private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
	private static final String GENERIC_SUCCESS_MSG = "If that email address is registered, an OTP has been sent to it.";
	private static final int OTP_COOLDOWN_SECONDS = 60;

	private final EmailService emailService;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final PasswordResetTokenRepository passwordResetTokenRepository;

	public PasswordResetService (
		EmailService emailService,
		UserRepository userRepository,
		BCryptPasswordEncoder passwordEncoder,
		PasswordResetTokenRepository passwordResetTokenRepository
	) {
		this.emailService = emailService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
	}

	@Transactional
	public MessageResponse requestPasswordReset ( PasswordResetRequest request ) {
		String email = request.email();
		log.info("Processing password reset request for email: {}", email);

		Optional< User > userOpt = userRepository.findUserByEmail(email);

		// prevent User Enumeration
		if ( userOpt.isEmpty() ) {
			log.warn("Password reset requested for non-existent email: {}", email);
			return MessageResponse.builder()
				.message(GENERIC_SUCCESS_MSG)
				.build();
		}

		User user = userOpt.get();

		handleActiveTokensAndCooldown(user);

		// create new password reset token and save it
		var resetToken = PasswordResetToken.builder()
			.user(user)
			.build();
		var savedResetToken = passwordResetTokenRepository.save(resetToken);

		// send email with the token to the user
		emailService.sendPasswordResetToken(user.getEmail(), user.getName(), savedResetToken.getToken());

		log.info("Password reset token generated and sent successfully to {}", email);
		return MessageResponse.builder()
			.message(GENERIC_SUCCESS_MSG)
			.build();
	}

	@Transactional
	public MessageResponse confirmPasswordReset ( PasswordResetConfirmationRequest request ) {
		String email = request.email();
		String token = request.token();
		String newPassword = request.newPassword();

		log.info("Processing password reset confirmation for user: {}", email);

		User user = userRepository.findUserByEmail(email)
			.orElseThrow(() -> new InvalidResetTokenException("Invalid request details"));

		PasswordResetToken recentlyCreatedToken = passwordResetTokenRepository.findFirstByUserOrderByCreatedAtDesc(user)
			.orElseThrow(() -> new InvalidResetTokenException("No password reset token found for the provided email"));

		validateTokenState(recentlyCreatedToken);

		// brute force check
		if ( ! recentlyCreatedToken.getToken().equals(token) ) {
			recentlyCreatedToken.incrementAttempts();

			if ( recentlyCreatedToken.isLocked() ) {
				recentlyCreatedToken.setUsed(true);
				log.warn("Password reset token is locked due to too many failed attempts for user: {}", email);
				throw new TokenExpiredException("This OTP is locked due to too many failed attempts. Please request a new one.");
			}
			throw new InvalidResetTokenException("Invalid OTP. Attempts left: " +
				( PasswordResetToken.MAX_ATTEMPTS - recentlyCreatedToken.getAttempts() ));
		}

		// check if new password is the same as the old password
		if ( passwordEncoder.matches(newPassword, user.getPassword()) ) {
			log.error("New password cannot be same as old password for user: {}", user.getEmail());
			throw new IllegalArgumentException("New password cannot be same as old password");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		recentlyCreatedToken.setUsed(true);

		log.info("Password reset successful for user: {}", user.getEmail());
		return MessageResponse.builder()
			.message("Password reset successful")
			.build();
	}

	@Transactional ( readOnly = true )
	public TokenValidationResponse validateResetToken ( String token ) {
		log.info("Validating password reset token: {}", token);
		boolean isValid = passwordResetTokenRepository.findByToken(token)
			.map(resetToken -> ! resetToken.isExpired() && ! resetToken.isUsed() && ! resetToken.isLocked())
			.orElse(false);

		return isValid ? TokenValidationResponse.valid(null) : TokenValidationResponse.invalid("Invalid or expired token");
	}

	/*
	 * private methods - is used internally only
	 */

	private void handleActiveTokensAndCooldown ( User user ) {
		List< PasswordResetToken > activeTokens = passwordResetTokenRepository.findAllByUser(user).stream()
			.filter(token -> ! token.isExpired() && ! token.isUsed() && ! token.isLocked())
			.toList();

		if ( ! activeTokens.isEmpty() ) {
			PasswordResetToken latestToken = activeTokens.getLast();

			TokenSecurityUtil.validateCooldown(latestToken.getCreatedAt(), OTP_COOLDOWN_SECONDS);

			activeTokens.forEach(token -> token.setUsed(true));
		}
	}

	private void validateTokenState ( PasswordResetToken token ) {
		String errorMsg = TokenSecurityUtil.getTokenStateError(
			token.isExpired(),
			token.isUsed(),
			token.isLocked()
		);
		if ( errorMsg != null ) {
			log.warn("Password reset token validation failed: {}", errorMsg);
			throw new TokenExpiredException(errorMsg);
		}
	}
}