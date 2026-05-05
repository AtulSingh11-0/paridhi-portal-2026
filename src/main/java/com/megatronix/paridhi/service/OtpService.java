package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.dto.request.OtpRequest;
import com.megatronix.paridhi.dto.request.OtpVerificationRequest;
import com.megatronix.paridhi.dto.response.MessageResponse;
import com.megatronix.paridhi.dto.response.OtpVerificationResponse;
import com.megatronix.paridhi.exception.InvalidEmailException;
import com.megatronix.paridhi.exception.InvalidOtpException;
import com.megatronix.paridhi.exception.OtpExpiredException;
import com.megatronix.paridhi.exception.UserAlreadyVerifiedException;
import com.megatronix.paridhi.model.OtpToken;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.OtpTokenRepository;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.security.JwtService;
import com.megatronix.paridhi.util.LoggingUtil;
import com.megatronix.paridhi.util.TokenSecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.megatronix.paridhi.constant.AppConstant.Common.FAILED;
import static com.megatronix.paridhi.constant.AppConstant.Common.SUCCESS;
import static com.megatronix.paridhi.constant.AppConstant.Resource.*;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.*;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static java.lang.String.format;

@Service
public class OtpService {

	private static final Logger log = LoggerFactory.getLogger(OtpService.class);
	private static final int OTP_COOLDOWN_SECONDS = 60;

	private final JwtService jwtService;
	private final EmailService emailService;
	private final UserRepository userRepository;
	private final OtpTokenRepository otpTokenRepository;

	public OtpService (
		JwtService jwtService,
		EmailService emailService,
		UserRepository userRepository,
		OtpTokenRepository otpTokenRepository
	) {
		this.jwtService = jwtService;
		this.emailService = emailService;
		this.userRepository = userRepository;
		this.otpTokenRepository = otpTokenRepository;
	}

	@Transactional
	public MessageResponse generateAndSendOtp ( OtpRequest request ) {
		return internalGenerateAndSendOtp(request);
	}

	@Transactional
	public OtpVerificationResponse verifyOtp ( OtpVerificationRequest request ) {
		String email = request.email();
		String otp = request.otp();

		if ( log.isDebugEnabled() ) {
			log.debug("Processing OTP verification request for email: {}", email);
		}

		// step-1: fetch the USER from the DB by email
		Optional< User > userOpt = userRepository.findUserByEmail(email);

		// step-2: guard - reject if no USER found with the provided email
		if ( userOpt.isEmpty() ) {
			LoggingUtil.logError(log, OTP_VERIFY, AppConstant.Service.OTP, null, format(NOT_FOUND_LOG, USER, EMAIL, email), null);
			throw new InvalidEmailException();
		}

		User user = userOpt.get();

		// step-3: guard — reject if the USER is already verified
		if ( user.isVerified() ) {
			LoggingUtil.logSecurity(log, OTP_VERIFY, user, FAILED, format(USER_ALREADY_VERIFIED, email));
			throw new UserAlreadyVerifiedException(USER_ALREADY_VERIFIED_USER_MESSAGE);
		}

		// step-4: fetch the most recent OTP token for this USER
		OtpToken latestOtp = otpTokenRepository.findFirstByUserOrderByCreatedAtDesc(user)
			.orElseThrow(() -> {
				LoggingUtil.logError(log, OTP_VERIFY, AppConstant.Service.OTP, user, format(OTP_NOT_FOUND, email), null);
				return new InvalidOtpException();
			});

		// step-5: validate the token state (expired / used / locked)
		validateTokenState(latestOtp, user, email);

		// step-6: compare the submitted OTP against the stored token
		if ( ! latestOtp.getOtp().equals(otp) ) {
			latestOtp.incrementAttempts();

			if ( latestOtp.isLocked() ) {
				// invalidate the token immediately on lockout
				latestOtp.setUsed(true);
				LoggingUtil.logSecurity(log, OTP_VERIFY, user, FAILED, format(OTP_LOCKED, email));
				throw new OtpExpiredException(OTP_LOCKED_USER_MESSAGE);
			}

			LoggingUtil.logSecurity(log, OTP_VERIFY, user, FAILED, format(OTP_INVALID, email));
			throw new InvalidOtpException(format(TOKEN_INVALID_USER_MESSAGE, OTP, OtpToken.MAX_ATTEMPTS - latestOtp.getAttempts()));
		}

		// step-7: mark USER as verified and invalidate the used token
		user.setVerified(true);
		latestOtp.setUsed(true);

		// step-8: generate a JWT token for the USER upon successful verification
		String jwtToken = jwtService.generateToken(user);
		LoggingUtil.logSecurity(log, OTP_VERIFY, user, SUCCESS, format(OTP_VERIFIED_LOG, email));

		return OtpVerificationResponse.valid(jwtToken);
	}

	@Transactional
	public MessageResponse resendOtp ( OtpRequest request ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Processing OTP resend request for email: {}", request.email());
		}

		// step-1: delegate to shared generation logic
		internalGenerateAndSendOtp(request);

		// step-2: log the resend audit milestone
		LoggingUtil.logOperation(log, MessageConstant.Operation.OTP_RESEND, AppConstant.Service.OTP, null, format(OTP_RESENT_LOG, request.email()));

		return MessageResponse.of(format(GENERIC_SUCCESS_MSG, OTP));
	}

	@Transactional
	@Scheduled ( cron = "0 0 * * * ?" )
	public void deleteExpiredOtps () {
		if ( log.isInfoEnabled() ) {
			log.info("Initiating scheduled cleanup of expired OTP tokens");
		}

		// delete all OTP tokens past their expiry
		otpTokenRepository.deleteAllExpiredTokens(Instant.now());
		LoggingUtil.logOperation(log, OTP_CLEANUP, AppConstant.Service.OTP, null, EXPIRED_OTPS_PURGED);
	}

	/*
	 * private methods — used internally only
	 */

	private MessageResponse internalGenerateAndSendOtp ( OtpRequest request ) {
		String email = request.email();

		if ( log.isDebugEnabled() ) {
			log.debug("Generating OTP for email: {}", email);
		}

		// step-1: fetch the USER from the DB by email
		Optional< User > userOpt = userRepository.findUserByEmail(email);

		// step-2: guard - reject if no USER found with the provided email
		if ( userOpt.isEmpty() ) {
			LoggingUtil.logSecurity(log, OTP_GENERATE, null, FAILED, format(NOT_FOUND_LOG, USER, EMAIL, email));
			return MessageResponse.of(format(GENERIC_SUCCESS_MSG, OTP));
		}

		User user = userOpt.get();

		// step-3: guard - reject if the USER is already verified
		if ( user.isVerified() ) {
			LoggingUtil.logSecurity(log, OTP_GENERATE, user, FAILED, format(USER_ALREADY_VERIFIED, email));
			throw new UserAlreadyVerifiedException(USER_ALREADY_VERIFIED_USER_MESSAGE);
		}

		// step-4: invalidate any active tokens and enforce cooldown window
		handleActiveTokensAndCooldown(user);

		// step-5: create and persist a new OTP token
		var otp = OtpToken.builder().user(user).build();
		var savedOtp = otpTokenRepository.save(otp);

		if ( log.isDebugEnabled() ) {
			log.debug("OTP token persisted with ID: {} for email: {}", savedOtp.getOtpId(), email);
		}

		// step-6: dispatch the OTP via email
		emailService.sendOtp(user.getEmail(), user.getName(), savedOtp.getOtp(), user);
		LoggingUtil.logOperation(log, OTP_GENERATE, AppConstant.Service.OTP, user, format(OTP_SENT_LOG, email));

		return MessageResponse.of(format(GENERIC_SUCCESS_MSG, OTP));
	}

	private void handleActiveTokensAndCooldown ( User user ) {
		List< OtpToken > activeTokens = otpTokenRepository.findAllByUser(user).stream()
			.filter(token -> ! token.isExpired() && ! token.isUsed() && ! token.isLocked())
			.toList();

		if ( ! activeTokens.isEmpty() ) {
			OtpToken latestToken = activeTokens.get(activeTokens.size() - 1);

			// enforce cooldown — throws CooldownException if too soon
			TokenSecurityUtil.validateCooldown(latestToken.getCreatedAt(), OTP_COOLDOWN_SECONDS);

			// invalidate all previously active tokens before issuing a new one
			activeTokens.forEach(token -> token.setUsed(true));
		}
	}

	private void validateTokenState ( OtpToken token, User user, String email ) {
		String errorMsg = TokenSecurityUtil.getTokenStateError(
			token.isExpired(),
			token.isUsed(),
			token.isLocked()
		);

		if ( errorMsg != null ) {
			LoggingUtil.logSecurity(log, OTP_VERIFY, user, FAILED, errorMsg + " — email: " + email);
			throw new OtpExpiredException(errorMsg);
		}
	}
}