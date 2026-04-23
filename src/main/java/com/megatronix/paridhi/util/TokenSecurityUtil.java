package com.megatronix.paridhi.util;

import java.time.Duration;
import java.time.Instant;

public final class TokenSecurityUtil {
	private TokenSecurityUtil() {
		// Private constructor prevents instantiation
	}

	/**
	 * Checks if enough time has passed since the last token request.
	 * Throws an exception if the cooldown is still active.
	 */
	public static void validateCooldown(Instant lastRequestTime, int cooldownSeconds) {
		long secondsSinceLastRequest = Duration.between(lastRequestTime, Instant.now()).getSeconds();

		if (secondsSinceLastRequest < cooldownSeconds) {
			throw new IllegalStateException("Please wait " + cooldownSeconds + " seconds before requesting a new token.");
		}
	}

	/**
	 * Checks the booleans of a token to see if it is valid.
	 * Returns an error message string if invalid, or null if valid.
	 */
	public static String getTokenStateError(boolean isExpired, boolean isUsed, boolean isLocked) {
		if (isUsed) return "This token has already been used.";
		if (isExpired) return "This token has expired. Please request a new one.";
		if (isLocked) return "This token is locked due to too many failed attempts.";
		return null; // Token is valid!
	}
}