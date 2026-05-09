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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mailtrap HTTP API email provider for the {@code staging} profile.
 * <p>
 * Sends transactional emails via {@code POST https://send.api.mailtrap.io/api/send}.
 * Implements exponential backoff with jitter for rate-limit (429) responses.
 * </p>
 *
 * @see <a href="https://api-docs.mailtrap.io/docs/mailtrap-api-docs/bcf61cdc1547e-send-email">Mailtrap API Docs</a>
 */
@Component
@Profile ( "staging" )
public class MailtrapEmailProvider implements EmailProvider {

	private static final Logger log = LoggerFactory.getLogger(MailtrapEmailProvider.class);

	private static final int MAX_RETRIES = 3;
	private static final long BASE_DELAY_MS = 1000L;
	private static final int JITTER_BOUND_MS = 500;

	private final RestClient restClient;

	@Value ( "${mailtrap.api.key}" )
	private String apiKey;

	@Value ( "${mailtrap.api.url:https://send.api.mailtrap.io/api/send}" )
	private String apiUrl;

	public MailtrapEmailProvider ( RestClient.Builder restClientBuilder ) {
		this.restClient = restClientBuilder
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@PostConstruct
	void init () {
		if ( apiKey == null || apiKey.isBlank() ) {
			log.error("⚠️ Mailtrap API key is not configured! Email sending will fail. Set MAILTRAP_API_KEY env variable.");
		} else {
			log.info("✅ Email provider initialized: {} (Mailtrap HTTP API)", getProviderName());
		}
	}

	@Override
	public void sendHtmlEmail ( String from, String[] to, String subject, String htmlBody ) {
		// Build recipient list in Mailtrap's expected format: [{"email": "..."}]
		List< Map< String, String > > recipients = Arrays.stream(to)
			.map(email -> Map.of("email", email))
			.toList();

		// Build the request payload per Mailtrap API spec
		Map< String, Object > payload = Map.of(
			"from", Map.of("email", from, "name", "Paridhi 2026"),
			"to", recipients,
			"subject", subject,
			"html", htmlBody
		);

		executeWithRetry(payload, subject, to.length);
	}

	@Override
	public String getProviderName () {
		return "mailtrap";
	}

	/**
	 * Executes the API call with exponential backoff retry on 429 (rate limited) responses.
	 */
	private void executeWithRetry ( Map< String, Object > payload, String subject, int recipientCount ) {
		for ( int attempt = 1; attempt <= MAX_RETRIES; attempt++ ) {
			try {
				restClient.post()
					.uri(apiUrl)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
					.body(payload)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, handle4xxError(subject))
					.onStatus(HttpStatusCode::is5xxServerError, handle5xxError(subject))
					.toBodilessEntity();

				if ( log.isDebugEnabled() ) {
					log.debug("Mailtrap API call successful | Subject: {} | Recipients: {}", subject, recipientCount);
				}
				return;

			} catch ( RateLimitException e ) {
				retryOrThrow(attempt, subject, "rate limit", new MailSendingException("Failed to send email due to rate limiting. Please try again later.", e));
			} catch ( MailSendingException e ) {
				throw e;

			} catch ( Exception e ) {
				log.error("Mailtrap unexpected error on attempt {}/{} | Subject: {} | Error: {}",
					attempt, MAX_RETRIES, subject, e.getMessage(), e);
				retryOrThrow(attempt, subject, "transient error",
					new MailSendingException("Failed to send email due to an unexpected error.", e));
			}
		}
	}

	private RestClient.ResponseSpec.ErrorHandler handle4xxError ( String subject ) {
		return ( request, response ) -> {
			int statusCode = response.getStatusCode().value();
			String body = new String(response.getBody().readAllBytes());

			if ( statusCode == 429 ) {
				log.warn("Mailtrap rate limit hit (429) | Subject: {} | Response: {}", subject, body);
				throw new RateLimitException("Mailtrap rate limit exceeded");
			}

			log.error("Mailtrap API client error {} | Subject: {} | Response: {}", statusCode, subject, body);
			throw new MailSendingException("Failed to send email due to an external service error.");
		};
	}

	private RestClient.ResponseSpec.ErrorHandler handle5xxError ( String subject ) {
		return ( request, response ) -> {
			int statusCode = response.getStatusCode().value();
			String body = new String(response.getBody().readAllBytes());

			log.error("Mailtrap API server error {} | Subject: {} | Response: {}", statusCode, subject, body);
			throw new MailSendingException("Failed to send email due to an external service error.");
		};
	}

	private void retryOrThrow ( int attempt, String subject, String reason, MailSendingException fallback ) {
		if ( attempt == MAX_RETRIES ) {
			log.error("Mailtrap {} exhausted after {} retries | Subject: {}", reason, MAX_RETRIES, subject);
			throw fallback;
		}
		long delay = calculateBackoff(attempt);
		log.warn("Mailtrap retry {}/{} after {}ms due to {} | Subject: {}", attempt, MAX_RETRIES, delay, reason, subject);
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
