package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.dto.request.OtpRequest;
import com.megatronix.paridhi.dto.request.OtpVerificationRequest;
import com.megatronix.paridhi.dto.response.MessageResponse;
import com.megatronix.paridhi.dto.response.OtpVerificationResponse;
import com.megatronix.paridhi.exception.InvalidOtpException;
import com.megatronix.paridhi.exception.OtpExpiredException;
import com.megatronix.paridhi.exception.UserAlreadyVerifiedException;
import com.megatronix.paridhi.exception.UserNotFoundException;
import com.megatronix.paridhi.model.OtpToken;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.OtpTokenRepository;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.util.LoggingUtil;
import com.megatronix.paridhi.util.TokenSecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OtpService {
	private static final Logger log = LoggerFactory.getLogger(OtpService.class);
	private static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: ";
	private static final String OTP_SERVICE = "OtpService";
	private static final int OTP_COOLDOWN_SECONDS = 60;

	private final EmailService emailService;
	private final UserRepository userRepository;
	private final OtpTokenRepository otpTokenRepository;

	public OtpService ( EmailService emailService, UserRepository userRepository, OtpTokenRepository otpTokenRepository ) {
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

		LoggingUtil.logOperation(log, MessageConstant.Operation.AUTHENTICATE, OTP_SERVICE, null, "Verifying OTP for user: " + email);

		User user = userRepository.findUserByEmail(email)
			.orElseThrow(() -> {
				LoggingUtil.logError(log, MessageConstant.Operation.AUTHENTICATE, OTP_SERVICE, null, USER_NOT_FOUND_WITH_EMAIL + email, null);
				return new UserNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.USER));
			});

		if ( user.isVerified() ) {
			LoggingUtil.logError(log, MessageConstant.Operation.AUTHENTICATE, OTP_SERVICE, null, "User is already verified: " + email, null);
			throw new UserAlreadyVerifiedException("User is already verified");
		}

		OtpToken latestOtp = otpTokenRepository.findFirstByUserOrderByCreatedAtDesc(user)
			.orElseThrow(() -> {
				LoggingUtil.logError(log, MessageConstant.Operation.AUTHENTICATE, OTP_SERVICE, null, "OTP not found for user: " + email, null);
				return new InvalidOtpException("Invalid request details");
			});

		validateTokenState(latestOtp, email);

		if ( ! latestOtp.getOtp().equals(otp) ) {
			latestOtp.incrementAttempts();

			if ( latestOtp.isLocked() ) {
				latestOtp.setUsed(true);
				LoggingUtil.logError(log, MessageConstant.Operation.AUTHENTICATE, OTP_SERVICE, null, "OTP locked due to 5 failed attempts for user: " + email, null);
				throw new OtpExpiredException("This OTP is locked due to too many failed attempts. Please request a new one.");
			}

			LoggingUtil.logError(log, MessageConstant.Operation.AUTHENTICATE, OTP_SERVICE, null, "Invalid OTP entered: " + otp, null);
			throw new InvalidOtpException("Invalid OTP. Attempts left: " + ( OtpToken.MAX_ATTEMPTS - latestOtp.getAttempts() ));
		}

		user.setVerified(true);
		latestOtp.setUsed(true);

		LoggingUtil.logOperation(log, MessageConstant.Operation.AUTHENTICATE, OTP_SERVICE, null, "OTP verified successfully for user: " + email + ", user is now verified");

		return OtpVerificationResponse.valid("OTP verified successfully");
	}

	@Transactional
	public MessageResponse resendOtp ( OtpRequest request ) {
		LoggingUtil.logOperation(log, MessageConstant.Operation.CREATE, OTP_SERVICE, null, "Resending OTP for user: " + request.email());

		internalGenerateAndSendOtp(request);

		LoggingUtil.logOperation(log, MessageConstant.Operation.CREATE, OTP_SERVICE, null, "OTP resent successfully for user: " + request.email());

		return new MessageResponse("OTP resent successfully");
	}

	@Transactional
	@Scheduled ( cron = "0 0 * * * ?" )
	public void deleteExpiredOtps () {
		LoggingUtil.logOperation(log, MessageConstant.Operation.DELETE, OTP_SERVICE, null, "Deleting expired OTPs");
		otpTokenRepository.deleteAllExpiredTokens(Instant.now());
		LoggingUtil.logOperation(log, MessageConstant.Operation.DELETE, OTP_SERVICE, null, "Successfully deleted expired OTPs");
	}

	/*
	 * private methods - is used internally only
	 */

	private MessageResponse internalGenerateAndSendOtp ( OtpRequest request ) {
		String email = request.email();
		LoggingUtil.logOperation(log, MessageConstant.Operation.CREATE, OTP_SERVICE, null, "Generating OTP for user: " + email);

		User user = userRepository.findUserByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found"));

		if ( user.isVerified() ) {
			throw new UserAlreadyVerifiedException("User is already verified");
		}

		handleActiveTokensAndCooldown(user);

		var otp = OtpToken.builder().user(user).build();
		var savedOtp = otpTokenRepository.save(otp);

		emailService.sendOtp(user.getEmail(), user.getName(), savedOtp.getOtp());
		return new MessageResponse("OTP sent successfully");
	}

	private void handleActiveTokensAndCooldown ( User user ) {
		List< OtpToken > activeTokens = otpTokenRepository.findAllByUser(user).stream()
			.filter(token -> ! token.isExpired() && ! token.isUsed() && ! token.isLocked())
			.toList();

		if ( ! activeTokens.isEmpty() ) {
			OtpToken latestToken = activeTokens.getLast();

			TokenSecurityUtil.validateCooldown(latestToken.getCreatedAt(), OTP_COOLDOWN_SECONDS);

			activeTokens.forEach(token -> token.setUsed(true));
		}
	}

	private void validateTokenState ( OtpToken token, String email ) {
		String errorMsg = TokenSecurityUtil.getTokenStateError(
			token.isExpired(),
			token.isUsed(),
			token.isLocked()
		);

		if ( errorMsg != null ) {
			LoggingUtil.logError(log, MessageConstant.Operation.AUTHENTICATE, OTP_SERVICE, null, errorMsg + " User: " + email, null);
			throw new OtpExpiredException(errorMsg);
		}
	}
}