package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.Position;
import com.megatronix.paridhi.exception.MailSendingException;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.util.EmailTemplateUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.megatronix.paridhi.constant.AppConstant.Common.UTF_8;
import static com.megatronix.paridhi.constant.AppConstant.Service.EMAIL;
import static com.megatronix.paridhi.constant.MessageConstant.EmailConstants.*;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.CREATE;
import static com.megatronix.paridhi.util.LoggingUtil.logError;
import static com.megatronix.paridhi.util.LoggingUtil.logOperation;
import static java.lang.String.format;

/**
 * Service for sending asynchronous emails for various system operations.
 */
@Service
public class EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	private final JavaMailSender javaMailSender;

	@Value ( "${spring.mail.username}" )
	private String fromEmail;

	public EmailService ( JavaMailSender javaMailSender ) {
		this.javaMailSender = javaMailSender;
	}

	/**
	 * Asynchronously sends a password reset token to a user.
	 */
	@Async
	public void sendPasswordResetToken ( String to, String name, String token, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Preparing to send password reset email to: {}", to);
		}

		// step-1: log the initiation of the operation
		logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENDING_LOG, PASSWORD_RESET, to));

		try {
			// step-2: create and configure the mime message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(format("%s - Password Reset Request", EMAIL_SUBJECT_PREFIX));
			helper.setText(EmailTemplateUtil.getResetTokenContent(name, token), true);

			// step-3: send the email
			javaMailSender.send(message);

			// step-4: log the successful completion
			logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENT_LOG, PASSWORD_RESET, to));

		} catch ( MessagingException e ) {
			// step-5: handle failure with audit logging
			logError(log, CREATE, EMAIL, user, format(EMAIL_SEND_FAILED_LOG, PASSWORD_RESET, to, e.getMessage()), e);
			throw new MailSendingException("Failed to send password reset email: " + e.getMessage(), e.getCause());
		}
	}

	/**
	 * Asynchronously sends an OTP verification code to a user.
	 */
	@Async
	public void sendOtp ( String to, String name, String otp, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Preparing to send OTP verification email to: {}", to);
		}

		// step-1: log the initiation
		logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENDING_LOG, OTP_VERIFICATION, to));

		try {
			// step-2: create and configure message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(format("%s - Email Verification OTP", EMAIL_SUBJECT_PREFIX));
			helper.setText(EmailTemplateUtil.getOtpContent(name, otp), true);

			// step-3: send email
			javaMailSender.send(message);

			// step-4: log success
			logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENT_LOG, OTP_VERIFICATION, to));

		} catch ( MessagingException e ) {
			// step-5: handle failure
			logError(log, CREATE, EMAIL, user, format(EMAIL_SEND_FAILED_LOG, OTP_VERIFICATION, to, e.getMessage()), e);
			throw new MailSendingException("Failed to send OTP email: " + e.getMessage(), e.getCause());
		}
	}

	/**
	 * Asynchronously sends a welcome email with MRD (Member Registration Details).
	 */
	@Async
	public void sendMRDWelcome ( String to, String name, String gid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Preparing to send MRD welcome email to: {} with GID: {}", to, gid);
		}

		// step-1: log initiation
		logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENDING_LOG, MRD_WELCOME, to));

		try {
			// step-2: configure message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(format("%s - Registration Confirmation", EMAIL_SUBJECT_PREFIX));
			helper.setText(EmailTemplateUtil.getMRDContent(name, gid), true);

			// step-3: send email
			javaMailSender.send(message);

			// step-4: log success
			logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENT_LOG, MRD_WELCOME, to));

		} catch ( MessagingException e ) {
			// step-5: handle failure
			logError(log, CREATE, EMAIL, user, format(EMAIL_SEND_FAILED_LOG, MRD_WELCOME, to, e.getMessage()), e);
			throw new MailSendingException("Failed to send MRD welcome email: " + e.getMessage(), e.getCause());
		}
	}

	/**
	 * Asynchronously sends event registration confirmation email to team members.
	 */
	@Async
	public void sendEventRegistration ( String[] to, String eventName, String teamName, String tid, User user ) {
		String recipientCount = to.length + RECIPIENTS;
		if ( log.isDebugEnabled() ) {
			log.debug("Preparing to send registration email to {} for event: {}", recipientCount, eventName);
		}

		// step-1: log initiation
		logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENDING_LOG, REGISTRATION, recipientCount));

		try {
			// step-2: configure message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(format("%s - Event Registration Confirmation", EMAIL_SUBJECT_PREFIX));
			helper.setText(EmailTemplateUtil.getRDContent(teamName, eventName, tid), true);

			// step-3: send email
			javaMailSender.send(message);

			// step-4: log success
			logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENT_LOG, REGISTRATION, recipientCount));

		} catch ( MessagingException e ) {
			// step-5: handle failure
			logError(log, CREATE, EMAIL, user, format(EMAIL_SEND_FAILED_LOG, REGISTRATION, recipientCount, e.getMessage()), e);
			throw new MailSendingException("Failed to send event registration email: " + e.getMessage(), e.getCause());
		}
	}

	/**
	 * Asynchronously sends qualification congratulations email to team members.
	 */
	@Async
	public void sendQualificationCongratulations ( String[] to, String eventName, String teamName, String tid, User user ) {
		String recipientCount = to.length + RECIPIENTS;
		if ( log.isDebugEnabled() ) {
			log.debug("Preparing to send qualification congratulations to {} for team: {}", recipientCount, teamName);
		}

		// step-1: log initiation
		logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENDING_LOG, QUALIFICATION, recipientCount));

		try {
			// step-2: configure message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(format("%s - Congratulations on Qualifying for Finals!", EMAIL_SUBJECT_PREFIX));
			helper.setText(EmailTemplateUtil.getQualificationContent(teamName, eventName, tid), true);

			// step-3: send email
			javaMailSender.send(message);

			// step-4: log success
			logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENT_LOG, QUALIFICATION, recipientCount));

		} catch ( MessagingException e ) {
			// step-5: handle failure
			logError(log, CREATE, EMAIL, user, format(EMAIL_SEND_FAILED_LOG, QUALIFICATION, recipientCount, e.getMessage()), e);
			throw new MailSendingException("Failed to send qualification congratulations: " + e.getMessage(), e.getCause());
		}
	}

	/**
	 * Asynchronously sends position/ranking congratulations email to team members.
	 */
	@Async
	public void sendPositionCongratulations ( String[] to, String eventName, String teamName, String tid, Position position, User user ) {
		String recipientCount = to.length + RECIPIENTS;
		if ( log.isDebugEnabled() ) {
			log.debug("Preparing to send position congratulations to {} for team: {}, position: {}", recipientCount, teamName, position);
		}

		// step-1: log initiation
		logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENDING_LOG, POSITION, recipientCount));

		try {
			// step-2: configure message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(format("%s - Congratulations on Securing %s Position!", EMAIL_SUBJECT_PREFIX, position));
			helper.setText(EmailTemplateUtil.getPositionContent(teamName, eventName, tid, position), true);

			// step-3: send email
			javaMailSender.send(message);

			// step-4: log success
			logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENT_LOG, POSITION, recipientCount));

		} catch ( MessagingException e ) {
			// step-5: handle failure
			logError(log, CREATE, EMAIL, user, format(EMAIL_SEND_FAILED_LOG, POSITION, recipientCount, e.getMessage()), e);
			throw new MailSendingException("Failed to send position congratulations: " + e.getMessage(), e.getCause());
		}
	}

	/**
	 * Asynchronously sends profile credentials to a new user.
	 */
	@Async
	public void sendProfileCredentials ( String to, String name, String password, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Preparing to send profile credentials to: {}", to);
		}

		// step-1: log initiation
		logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENDING_LOG, CREDENTIALS, to));

		try {
			// step-2: configure message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(format("%s - Your Account Credentials", EMAIL_SUBJECT_PREFIX));
			helper.setText(EmailTemplateUtil.getProfileCredentialsContent(name, to, password), true);

			// step-3: send email
			javaMailSender.send(message);

			// step-4: log success
			logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENT_LOG, CREDENTIALS, to));
		} catch ( MessagingException e ) {
			// step-5: handle failure
			logError(log, CREATE, EMAIL, user, format(EMAIL_SEND_FAILED_LOG, CREDENTIALS, to, e.getMessage()), e);
			throw new MailSendingException("Failed to send profile credentials: " + e.getMessage(), e.getCause());
		} catch ( Exception e ) {
			logError(log, CREATE, EMAIL, user, "Unexpected error sending profile credentials to: " + to + ". Error: " + e.getMessage(), e);
			throw new MailSendingException("Unexpected error sending profile credentials: " + e.getMessage(), e.getCause());
		}
	}

	/**
	 * Asynchronously sends a consolidated registration email to a user.
	 */
	@Async
	public void sendConsolidatedRegistrationEmail ( String to, String name, String password, String gid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Preparing to send consolidated registration email to: {}", to);
		}

		// step-1: log initiation
		logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENDING_LOG, format("%s & %s", MRD_WELCOME, CREDENTIALS), to));

		try {
			// step-2: configure message
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(fromEmail);
			helper.setTo(to);
			helper.setSubject(format("%s - Registration Confirmation & Credentials", EMAIL_SUBJECT_PREFIX));
			helper.setText(EmailTemplateUtil.getConsolidatedEmailContent(to, name, password, gid), true);

			// step-3: send email
			javaMailSender.send(message);

			// step-4: log success
			logOperation(log, CREATE, EMAIL, user, format(EMAIL_SENT_LOG, format("%s & %s", MRD_WELCOME, CREDENTIALS), to));
		} catch ( MessagingException e ) {
			// step-5: handle failure
			logError(log, CREATE, EMAIL, user, format(EMAIL_SEND_FAILED_LOG, "consolidated registration",
				to, e.getMessage()), e);
			throw new MailSendingException("Failed to send consolidated registration email: " + e.getMessage(), e.getCause());
		}
	}
}
