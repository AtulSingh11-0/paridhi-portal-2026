package com.megatronix.paridhi.service.email;

import com.megatronix.paridhi.exception.MailSendingException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Brevo (formerly Sendinblue) HTTP API email provider for the {@code prod} profile.
 * <p>
 * Sends transactional emails via {@code POST https://api.brevo.com/v3/smtp/email}.
 * Implements exponential backoff with jitter for rate-limit (429) responses.
 * </p>
 *
 * @see <a href="https://developers.brevo.com/reference/sendtransacemail">Brevo API Docs</a>
 */
@Component
@Profile ( "prod" )
public class BrevoEmailProvider implements EmailProvider {

	private static final Logger log = LoggerFactory.getLogger(BrevoEmailProvider.class);

	private static final int MAX_RETRIES = 3;
	private static final long BASE_DELAY_MS = 1000L;
	private static final int JITTER_BOUND_MS = 500;

	private final RestClient restClient;

	@Value ( "${brevo.api.key}" )
	private String apiKey;

	@Value ( "${brevo.api.url:https://api.brevo.com/v3/smtp/email}" )
	private String apiUrl;

	public BrevoEmailProvider ( RestClient.Builder restClientBuilder ) {
		this.restClient = restClientBuilder
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@PostConstruct
	void init () {
		if ( apiKey == null || apiKey.isBlank() ) {
			log.error("⚠️ Brevo API key is not configured! Email sending will fail. Set BREVO_API_KEY env variable.");
		} else {
			log.info("✅ Email provider initialized: {} (Brevo HTTP API)", getProviderName());
		}
	}

	@Override
	public void sendHtmlEmail ( String from, String[] to, String subject, String htmlBody ) {
		// Build recipient list in Brevo's expected format: [{"email": "..."}]
		List< Map< String, String > > recipients = Arrays.stream(to)
			.map(email -> Map.of("email", email))
			.toList();

		// Build the request payload per Brevo API spec
		// Using HashMap because Brevo requires mutable map for optional fields
		Map< String, Object > payload = new HashMap<>();
		payload.put("sender", Map.of("email", from, "name", "Paridhi 2026"));
		payload.put("to", recipients);
		payload.put("subject", subject);
		payload.put("htmlContent", htmlBody);

		executeWithRetry(payload, subject, to.length);
	}

	@Override
	public String getProviderName () {
		return "brevo";
	}

	private void executeWithRetry ( Map< String, Object > payload, String subject, int recipientCount ) {
		for ( int attempt = 1; attempt <= MAX_RETRIES; attempt++ ) {
			try {
				restClient.post()
					.uri(apiUrl)
					.header("api-key", apiKey)
					.body(payload)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, handle4xxError(subject))
					.onStatus(HttpStatusCode::is5xxServerError, handle5xxError(subject))
					.toBodilessEntity();

				if ( log.isDebugEnabled() ) {
					log.debug("Brevo API call successful | Subject: {} | Recipients: {}", subject, recipientCount);
				}
				return;

			} catch ( RateLimitException e ) {
				retryOrThrow(attempt, subject, "rate limit",
					new MailSendingException("Failed to send email due to rate limiting. Please try again later.", e));

			} catch ( MailSendingException e ) {
				throw e;

			} catch ( Exception e ) {
				log.error("Brevo unexpected error on attempt {}/{} | Subject: {} | Error: {}",
					attempt, MAX_RETRIES, subject, e.getMessage(), e);
				retryOrThrow(attempt, subject, "transient error",
					new MailSendingException("Failed to send email due to an unexpected error.", e));
			}
		}
	}

	private ErrorHandler handle4xxError ( String subject ) {
		return ( request, response ) -> {
			int statusCode = response.getStatusCode().value();
			String body = new String(response.getBody().readAllBytes());

			if ( statusCode == 429 ) {
				log.warn("Brevo rate limit hit (429) | Subject: {} | Response: {}", subject, body);
				throw new RateLimitException("Brevo rate limit exceeded");
			}

			log.error("Brevo API client error {} | Subject: {} | Response: {}", statusCode, subject, body);
			throw new MailSendingException("Failed to send email due to an external service error.");
		};
	}

	private ErrorHandler handle5xxError ( String subject ) {
		return ( request, response ) -> {
			int statusCode = response.getStatusCode().value();
			String body = new String(response.getBody().readAllBytes());

			log.error("Brevo API server error {} | Subject: {} | Response: {}", statusCode, subject, body);
			throw new MailSendingException("Failed to send email due to an external service error.");
		};
	}

	private void retryOrThrow ( int attempt, String subject, String reason, MailSendingException fallback ) {
		if ( attempt == MAX_RETRIES ) {
			log.error("Brevo {} exhausted after {} retries | Subject: {}", reason, MAX_RETRIES, subject);
			throw fallback;
		}
		long delay = calculateBackoff(attempt);
		log.warn("Brevo retry {}/{} after {}ms due to {} | Subject: {}", attempt, MAX_RETRIES, delay, reason, subject);
		sleep(delay);
	}

	/**
	 * Calculates exponential backoff delay with jitter to avoid thundering herd.
	 */
	private long calculateBackoff ( int attempt ) {
		long exponentialDelay = BASE_DELAY_MS * ( 1L << ( attempt - 1 ) );
		long jitter = ThreadLocalRandom.current().nextLong(JITTER_BOUND_MS);
		return exponentialDelay + jitter;
	}

	private void sleep ( long millis ) {
		try {
			Thread.sleep(millis);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new MailSendingException("Interrupted during retry backoff", e);
		}
	}

	/**
	 * Internal exception to signal rate-limit responses for retry logic.
	 */
	private static class RateLimitException extends RuntimeException {
		RateLimitException ( String message ) {
			super(message);
		}
	}
}
