package com.megatronix.paridhi.service.email;

/**
 * Strategy interface for email sending providers.
 * Implementations are selected based on the active Spring profile:
 * <ul>
 *     <li>{@code default / dev} → SMTP (JavaMailSender)</li>
 *     <li>{@code staging} → Mailtrap HTTP API</li>
 *     <li>{@code prod} → Brevo HTTP API</li>
 * </ul>
 */
public interface EmailProvider {

	/**
	 * Sends an HTML email to one or more recipients.
	 *
	 * @param from     sender email address
	 * @param to       array of recipient email addresses
	 * @param subject  email subject line
	 * @param htmlBody the HTML content body
	 */
	void sendHtmlEmail ( String from, String[] to, String subject, String htmlBody );

	/**
	 * Returns the provider name for audit logging and startup diagnostics.
	 */
	String getProviderName ();
}
