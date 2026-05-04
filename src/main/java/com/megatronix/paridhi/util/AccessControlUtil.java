package com.megatronix.paridhi.util;

import com.megatronix.paridhi.constant.Role;
import com.megatronix.paridhi.exception.ForbiddenAccessException;
import com.megatronix.paridhi.model.User;
import org.slf4j.Logger;

import static com.megatronix.paridhi.constant.AppConstant.FAILED;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.AUTHENTICATION_REQUIRED;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.NOT_AUTHORIZED;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.ACCESS_DENIED;
import static java.lang.String.format;

/**
 * Utility class for enforcing role-based access control on service methods.
 * <p>
 * Centralizes the repeated guard pattern used across all admin-only services:
 * rejects unauthenticated (null) users and regular users with ROLE_USER, and
 * emits structured security audit logs via {@link LoggingUtil} for every decision.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * AccessControlUtil.requireAdminAccess(log, user, EVENT_CREATE, EVENT);
 * }</pre>
 */
public final class AccessControlUtil {

	private AccessControlUtil () {
		// Utility class — no instantiation
	}

	/**
	 * Asserts that the given user is authenticated and holds an elevated role
	 * (i.e. is NOT {@code ROLE_USER}).
	 *
	 * @param log       the caller's SLF4J logger (used for security audit logs)
	 * @param user      the authenticated user, or {@code null} if unauthenticated
	 * @param operation a short descriptor of the attempted operation (e.g. {@code EVENT_CREATE})
	 * @param resource  the resource type being acted upon (e.g. {@code "Event"})
	 *
	 * @throws ForbiddenAccessException if the user is null or has {@code ROLE_USER}
	 */
	public static void requireAdminAccess ( Logger log, User user, String operation, String resource ) {
		// step-1: reject unauthenticated (null) users outright
		if ( user == null ) {
			LoggingUtil.logSecurity(log, ACCESS_DENIED, null, FAILED,
				format(AUTHENTICATION_REQUIRED, operation + " " + resource));
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}

		// step-2: reject regular users — only ADMIN and above may proceed
		if ( user.getRole().equals(Role.ROLE_USER) ) {
			LoggingUtil.logSecurity(log, ACCESS_DENIED, user, FAILED,
				format(NOT_AUTHORIZED, operation + " " + resource));
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}

		// step-3: access granted — log at debug level for observability
		if ( log.isDebugEnabled() ) {
			log.debug("Access granted to user: {} (ID: {}) for operation: {} on resource: {}",
				user.getEmail(), user.getUserId(), operation, resource);
		}
	}
}
