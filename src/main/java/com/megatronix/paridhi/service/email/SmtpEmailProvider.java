package com.megatronix.paridhi.service.email;

import com.megatronix.paridhi.exception.MailSendingException;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import static com.megatronix.paridhi.constant.AppConstant.Common.UTF_8;

/**
 * SMTP-based email provider using Spring's {@link JavaMailSender}.
 * Active for local development and the {@code dev} profile.
 */
@Component
@Profile ( { "default", "dev" } )
public class SmtpEmailProvider implements EmailProvider {

	private static final Logger log = LoggerFactory.getLogger(SmtpEmailProvider.class);
	private final JavaMailSender javaMailSender;

	public SmtpEmailProvider ( JavaMailSender javaMailSender ) {
		this.javaMailSender = javaMailSender;
	}

	@PostConstruct
	void init () {
		log.info("✅ Email provider initialized: {} (SMTP)", getProviderName());
	}

	@Override
	public void sendHtmlEmail ( String from, String[] to, String subject, String htmlBody ) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlBody, true);

			javaMailSender.send(message);
		} catch ( MessagingException e ) {
			log.error("SMTP email sending failed | Subject: {} | Recipients: {} | Error: {}",
				subject, to.length, e.getMessage());
			throw new MailSendingException("Failed to send email due to an internal server error.", e);
		}
	}

	@Override
	public String getProviderName () {
		return "smtp";
	}
}
