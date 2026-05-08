package com.megatronix.paridhi.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

/**
 * Utility class for managing the Spring Security context for auditing purposes.
 * This is particularly useful for whitelisted operations (like registration or password reset)
 * where a user is not yet authenticated via JWT but their identity is known.
 */
public final class SecurityContextUtil {

	private SecurityContextUtil () {
		// Prevent instantiation
	}

	/**
	 * Temporarily sets the security context with a mock authentication containing the user's email.
	 * This allows JPA auditing (@CreatedBy, @LastModifiedBy) to capture the correct user.
	 *
	 * @param email The user's email to set as the auditor
	 */
	public static void setAuditUser ( String email ) {
		var authentication = new UsernamePasswordAuthenticationToken(
			email,
			null,
			Collections.emptyList()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * Clears the security context. Should be called in a finally block after setAuditUser.
	 */
	public static void clear () {
		SecurityContextHolder.clearContext();
	}
}
