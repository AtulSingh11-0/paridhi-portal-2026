package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
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
import com.megatronix.paridhi.util.LoggingUtil;
import com.megatronix.paridhi.util.TokenSecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.megatronix.paridhi.constant.AppConstant.Common.FAILED;
import static com.megatronix.paridhi.constant.AppConstant.Common.SUCCESS;
import static com.megatronix.paridhi.constant.AppConstant.Resource.EMAIL;
import static com.megatronix.paridhi.constant.AppConstant.Resource.USER;
import static com.megatronix.paridhi.constant.AppConstant.Service.PASSWORD_RESET;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.*;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static com.megatronix.paridhi.constant.MessageConstant.ToastMessage.PASSWORD_RESET_SUCCESSFUL;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.PASSWORD_RESET_TOKEN_INVALID_OR_EXPIRED;
import static java.lang.String.format;

@Service
public class PasswordResetService {

	private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
	private static final int OTP_COOLDOWN_SECONDS = 60 * 30; // 30 minutes

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

		if ( log.isDebugEnabled() ) {
			log.debug("Processing password reset request for email: {}", email);
		}

		// step-1: fetch the USER from the DB by email
		Optional< User > userOpt = userRepository.findUserByEmail(email);

		// step-2: guard — prevent user enumeration by returning generic success even if user not found
		if ( userOpt.isEmpty() ) {
			LoggingUtil.logSecurity(log, PASSWORD_RESET_REQUEST, null, FAILED, format(NOT_FOUND_LOG, USER, EMAIL,
				email));
			return MessageResponse.of(format(GENERIC_SUCCESS_MSG, AppConstant.Resource.PASSWORD_RESET));
		}

		User user = userOpt.get();

		// step-3: invalidate any active tokens and enforce cooldown window
		handleActiveTokensAndCooldown(user);

		// step-4: create and persist a new password reset token
		var resetToken = PasswordResetToken.builder()
			.user(user)
			.build();
		var savedResetToken = passwordResetTokenRepository.save(resetToken);

		if ( log.isDebugEnabled() ) {
			log.debug("Password reset token persisted for user: {} (ID: {})", email, user.getUserId());
		}

		// step-5: dispatch the reset token via email
		emailService.sendPasswordResetToken(user.getEmail(), user.getName(), savedResetToken.getToken(), user);
		LoggingUtil.logOperation(log, PASSWORD_RESET_REQUEST, PASSWORD_RESET, user,
			format(PASSWORD_RESET_SENT_LOG, email));

		return MessageResponse.of(format(GENERIC_SUCCESS_MSG, PASSWORD_RESET));
	}

	@Transactional
	public MessageResponse confirmPasswordReset ( PasswordResetConfirmationRequest request ) {
		String email = request.email();
		String token = request.token();
		String newPassword = request.newPassword();

		if ( log.isDebugEnabled() ) {
			log.debug("Processing password reset confirmation for email: {}", email);
		}

		// step-1: fetch the USER from the DB by email
		User user = userRepository.findUserByEmail(email)
			.orElseThrow(() -> {
				LoggingUtil.logError(log, PASSWORD_RESET_CONFIRM, PASSWORD_RESET, null,
					format(NOT_FOUND_LOG, USER, EMAIL, email), null);
				return new InvalidResetTokenException(PASSWORD_RESET_TOKEN_INVALID_OR_EXPIRED);
			});

		// step-2: fetch the most recent reset token for this USER
		PasswordResetToken latestToken = passwordResetTokenRepository.findFirstByUserOrderByCreatedAtDesc(user)
			.orElseThrow(() -> {
				LoggingUtil.logError(log, PASSWORD_RESET_CONFIRM, PASSWORD_RESET, user,
					format(PASSWORD_RESET_TOKEN_NOT_FOUND, email), null);
				return new InvalidResetTokenException(PASSWORD_RESET_TOKEN_INVALID_OR_EXPIRED);
			});

		// step-3: validate the token state (expired / used / locked)
		validateTokenState(latestToken, user, email);

		// step-4: brute force protection — compare the submitted token against the
		// stored token
		if ( ! latestToken.getToken().equals(token) ) {
			latestToken.incrementAttempts();

			if ( latestToken.isLocked() ) {
				// invalidate the token immediately on lockout
				latestToken.setUsed(true);
				LoggingUtil.logSecurity(log, PASSWORD_RESET_CONFIRM, user, FAILED, format(PASSWORD_RESET_TOKEN_LOCKED, email));
				throw new TokenExpiredException(OTP_LOCKED_USER_MESSAGE);
			}

			LoggingUtil.logSecurity(log, PASSWORD_RESET_CONFIRM, user, FAILED, format(PASSWORD_RESET_TOKEN_INVALID, email));
			throw new InvalidResetTokenException(format(TOKEN_INVALID_USER_MESSAGE, PASSWORD_RESET, PasswordResetToken.MAX_ATTEMPTS - latestToken.getAttempts()));
		}

		// step-5: guard — ensure new password is not identical to current password
		if ( passwordEncoder.matches(newPassword, user.getPassword()) ) {
			LoggingUtil.logSecurity(log, PASSWORD_RESET_CONFIRM, user, FAILED, format(PASSWORD_RESET_SAME_PASSWORD, email));
			throw new IllegalArgumentException(PASSWORD_RESET_SAME_PASSWORD_USER_MESSAGE);
		}

		// step-6: encode and update the new password, then invalidate the token
		user.setPassword(passwordEncoder.encode(newPassword));
		latestToken.setUsed(true);

		LoggingUtil.logSecurity(log, PASSWORD_RESET_CONFIRM, user, SUCCESS, format(PASSWORD_RESET_SUCCESS_LOG, email));

		return MessageResponse.of(PASSWORD_RESET_SUCCESSFUL);
	}

	@Transactional ( readOnly = true )
	public TokenValidationResponse validateResetToken ( String token ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Validating password reset token");
		}

		// step-1: lookup token and verify its validity state
		Optional< PasswordResetToken > tokenOpt = passwordResetTokenRepository.findByToken(token);

		// step-2: guard — reject if token not found or in invalid state (expired / used / locked)
		if ( tokenOpt.isEmpty() ) {
			LoggingUtil.logError(log, PASSWORD_RESET_VALIDATE, PASSWORD_RESET, null, format(PASSWORD_RESET_TOKEN_NOT_FOUND, token), null);
			throw new InvalidResetTokenException(PASSWORD_RESET_TOKEN_INVALID_OR_EXPIRED);
		}

		PasswordResetToken resetToken = tokenOpt.get();

		boolean isValid = ! resetToken.isExpired() && ! resetToken.isUsed() && ! resetToken.isLocked();

		if ( isValid ) {
			User user = resetToken.getUser();
			LoggingUtil.logOperation(log, PASSWORD_RESET_VALIDATE, PASSWORD_RESET, user,
				format(PASSWORD_RESET_VALIDATED_LOG, user.getEmail()));
			return TokenValidationResponse.valid(null);
		}
		throw new InvalidResetTokenException(PASSWORD_RESET_TOKEN_INVALID_OR_EXPIRED);
	}

	/*
	 * private methods — used internally only
	 */

	private void handleActiveTokensAndCooldown ( User user ) {
		// step-1: fetch all tokens that haven't been finalized yet
		List< PasswordResetToken > activeTokens = passwordResetTokenRepository.findAllByUser(user).stream()
			.filter(token -> ! token.isExpired() && ! token.isUsed() && ! token.isLocked())
			.toList();

		if ( ! activeTokens.isEmpty() ) {
			PasswordResetToken latestToken = activeTokens.get(activeTokens.size() - 1);

			// enforce cooldown — throws CooldownException if too soon
			TokenSecurityUtil.validateCooldown(latestToken.getCreatedAt(), OTP_COOLDOWN_SECONDS);

			// invalidate all previously active tokens before issuing a new one
			activeTokens.forEach(token -> token.setUsed(true));
		}
	}

	private void validateTokenState ( PasswordResetToken token, User user, String email ) {
		// step-1: check for expiry, usage, or lockout status
		String errorMsg = TokenSecurityUtil.getTokenStateError(
			token.isExpired(),
			token.isUsed(),
			token.isLocked());

		if ( errorMsg != null ) {
			LoggingUtil.logSecurity(log, PASSWORD_RESET_CONFIRM, user, FAILED, errorMsg + " — email: " + email);
			throw new TokenExpiredException(errorMsg);
		}
	}
}