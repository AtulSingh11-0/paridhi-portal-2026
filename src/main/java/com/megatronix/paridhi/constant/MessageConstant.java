package com.megatronix.paridhi.constant;

/**
 * Constants related to messages displayed to users or isUsed in logs.
 * Organized into inner classes by purpose and audience.
 */
public final class MessageConstant {
	private MessageConstant () {
		// Private constructor to prevent instantiation
	}

	/**
	 * Operation types for consistent logging
	 */
	public static final class Operation {
		private Operation () {
		}

		// AUTH operations
		public static final String USER_REGISTER = "USER_REGISTER";
		public static final String USER_LOGIN = "USER_LOGIN";
		public static final String USER_LOGOUT = "USER_LOGOUT";
		public static final String ADMIN_CREATE = "ADMIN_CREATE";

		public static final String TOKEN_VALIDATION = "TOKEN_VALIDATION";
		public static final String TOKEN_CLEANUP = "TOKEN_CLEANUP";

		// EVENT operations
		public static final String EVENT_CREATE = "EVENT_CREATE";
		public static final String EVENT_UPDATE = "EVENT_UPDATE";
		public static final String EVENT_DELETE = "EVENT_DELETE";
		public static final String EVENT_TOGGLE_REGISTRATION = "EVENT_TOGGLE_REGISTRATION";
		public static final String EVENT_IMAGE_UPDATE = "EVENT_IMAGE_UPDATE";

		// EVENT COMBO operations
		public static final String COMBO_CREATE = "COMBO_CREATE";
		public static final String COMBO_UPDATE = "COMBO_UPDATE";
		public static final String COMBO_DELETE = "COMBO_DELETE";
		public static final String COMBO_TOGGLE_REGISTRATION = "COMBO_TOGGLE_REGISTRATION";
		public static final String COMBO_IMAGE_UPDATE = "COMBO_IMAGE_UPDATE";
		public static final String COMBO_ADD_EVENT = "COMBO_ADD_EVENT";
		public static final String COMBO_REMOVE_EVENT = "COMBO_REMOVE_EVENT";

		// OTP operations
		public static final String OTP_GENERATE = "OTP_GENERATE";
		public static final String OTP_VERIFY = "OTP_VERIFY";
		public static final String OTP_RESEND = "OTP_RESEND";
		public static final String OTP_CLEANUP = "OTP_CLEANUP";

		// PASSWORD RESET operations
		public static final String PASSWORD_RESET_REQUEST = "PASSWORD_RESET_REQUEST";
		public static final String PASSWORD_RESET_CONFIRM = "PASSWORD_RESET_CONFIRM";
		public static final String PASSWORD_RESET_VALIDATE = "PASSWORD_RESET_VALIDATE";

		// Basic CRUD operations
		public static final String CREATE = "create";
		public static final String READ = "read";
		public static final String UPDATE = "update";
		public static final String DELETE = "delete";

		// Other operations
		public static final String RESOLVE = "resolve";
	}

	/**
	 * Generic user-facing messages that don't expose implementation details
	 */
	public static final class UserMessage {
		private UserMessage () {
		}

		public static final String INVALID_CREDENTIALS = "Invalid credentials.";
		public static final String INVALID_EMAIL = "Invalid email.";
		public static final String USER_ALREADY_EXISTS = "This email is already registered. Log in instead or Reset your password.";

		public static final String INVALID_AUTH_HEADER = "Authorization header is missing or invalid";
		public static final String JWT_TOKEN_REVOKED = "JWT token has been revoked";
		public static final String JWT_TOKEN_EXPIRED = "JWT token has expired";
		public static final String JWT_TOKEN_INVALID = "JWT token is invalid";
		public static final String JWT_TOKEN_VALIDATION_FAILED = "Token validation failed";

		public static final String PASSWORD_RESET_TOKEN_INVALID_OR_EXPIRED = "Password reset token is INVALID or EXPIRED.";

		// Authentication and Authorization
		public static final String ACCESS_DENIED = "You don't have permission to access this resource.";

		// General errors
		public static final String CONFLICT_ERROR = "The request could not be completed due to a conflict with current state.";
		public static final String FILE_UPLOAD_ERROR = "There was an error uploading the file. Please try again.";
		public static final String FILE_DELETION_ERROR = "There was an error deleting the file. Please try again.";
	}

	/**
	 * Common error message templates for exceptions and error logs
	 */
	public static final class ErrorTemplate {
		private ErrorTemplate () {
		}

		// General error templates
		public static final String NOT_FOUND = "%s not found.";
		public static final String EMAIL_NOT_FOUND = "User not found with email: %s.";
		public static final String NOT_FOUND_LOG = "%s not found with %s: %s.";
		public static final String ALREADY_EXISTS = "%s already exists with %s: %s.";

		// Authentication/Authorization errors
		public static final String NOT_AUTHORIZED = "User not authorized to %s";
		public static final String AUTHENTICATION_REQUIRED = "Authentication required to %s";

		// Token related errors
		public static final String JWT_TOKEN_BLACKLISTED = "JWT token has been blacklisted";
		public static final String JWT_TOKEN_VALIDATION_FAILED = "JWT token validation failed unexpectedly";

		// Team/Event related errors
		public static final String TEAM_SIZE = "Team size must be between: %d and %d for event: %s";
		public static final String REGISTRATION_CLOSED = "Registration is closed for %s: %s";
		public static final String MISSING_GID_LIST = "GID list is required for team registration for %s: %s";

		// operation errors
		public static final String OPERATION_FAILED = "Failed to %s: %s - Error: %s";

		// invalid domain exception
		public static final String INVALID_DOMAIN = "All events must belong to the same domain: %s";

		// OTP related errors
		public static final String OTP_NOT_FOUND = "OTP not found for user with email: %s.";
		public static final String OTP_LOCKED = "OTP locked due to too many failed attempts for user with email: %s.";
		public static final String OTP_INVALID = "Invalid OTP entered for user with email: %s.";
		public static final String TOKEN_INVALID_USER_MESSAGE = "Invalid %s. Only %d Attempts left.";
		public static final String OTP_LOCKED_USER_MESSAGE = "This OTP is locked due to too many failed attempts. Please request a new one.";
		public static final String USER_ALREADY_VERIFIED = "User is already verified with email: %s.";
		public static final String USER_ALREADY_VERIFIED_USER_MESSAGE = "Your account is already verified.";

		// Password Reset related errors
		public static final String PASSWORD_RESET_TOKEN_NOT_FOUND = "Password reset token not found for user with email: %s.";
		public static final String PASSWORD_RESET_TOKEN_LOCKED = "Password reset token locked due to too many failed attempts for user with email: %s.";
		public static final String PASSWORD_RESET_TOKEN_INVALID = "Invalid password reset token entered for user with email: %s.";
		public static final String PASSWORD_RESET_SAME_PASSWORD = "New password cannot be same as old password for user with email: %s.";
		public static final String PASSWORD_RESET_SAME_PASSWORD_USER_MESSAGE = "New password cannot be the same as your old password.";

		// Profile related errors
		public static final String PROFILE_ALREADY_EXISTS = "Profile already exists for user: %s.";
		public static final String PROFILE_NOT_CREATED = "Profile not yet created for user: %s.";
		public static final String USER_UNVERIFIED = "User email must be verified to perform this action. User: %s.";
	}

	/**
	 * Common success message templates
	 */
	public static final class SuccessTemplate {
		private SuccessTemplate () {
		}

		// CRUD operation success messages
		public static final String CREATED = "%s created successfully with ID: %s.";
		public static final String UPDATED = "%s updated successfully with ID: %s.";
		public static final String DELETED = "%s deleted successfully with ID: %s.";
		public static final String FETCHED = "Successfully retrieved %s.";
		public static final String FETCHED_COUNT = "Successfully retrieved %d %s.";

		public static final String USER_AUTHENTICATED_SUCCESSFULLY = "User authenticated successfully with email: %s.";

		// Authentication/Authorization errors
		public static final String AUTHORIZED = "User authorized to %s.";
	}

	/**
	 * Detailed log messages for debugging and monitoring
	 */
	public static final class LogMessage {
		private LogMessage () {
		}

		public static final String EXPIRED_BLACKLISTED_TOKENS_PURGED = "Expired blacklisted tokens purged";

		// OTP messages
		public static final String OTP_SENT_LOG = "OTP generated and sent successfully to user with email: %s.";
		public static final String OTP_RESENT_LOG = "OTP resent successfully to user with email: %s.";
		public static final String OTP_VERIFIED_LOG = "OTP verified successfully — user is now verified. Email: %s.";
		public static final String EXPIRED_OTPS_PURGED = "Expired OTP tokens purged.";

		// Password Reset messages
		public static final String PASSWORD_RESET_SENT_LOG = "Password reset token generated and sent successfully to user with email: %s.";
		public static final String PASSWORD_RESET_SUCCESS_LOG = "Password reset successful for user with email: %s.";
		public static final String PASSWORD_RESET_VALIDATED_LOG = "Password reset token validated successfully for email: %s.";

		public static final String GENERIC_SUCCESS_MSG = "If that email address is registered, a %s has been sent to " +
			"it.";

		// Event messages
		public static final String EVENT_CREATED = "Event '%s' (ID: %s) created by user: %s (ID: %s).";
		public static final String EVENT_UPDATED = "Event '%s' (ID: %s) updated by user: %s (ID: %s).";
		public static final String EVENT_DELETED = "Event '%s' (ID: %s) deleted by user: %s (ID: %s).";
		public static final String EVENT_RETRIEVED = "Event '%s' (ID: %s) retrieved.";
		public static final String EVENTS_RETRIEVED = "%d events retrieved.";
		public static final String EVENT_NOT_FOUND = "Event not found with ID: %s.";
		public static final String EVENT_REGISTRATION_TOGGLED = "Event '%s' (ID: %s) registration toggled: %s -> %s by user: %s (ID: %s).";
		public static final String EVENT_IMAGE_UPDATED = "Event '%s' (ID: %s) image updated. New fileId: %s. By user: %s (ID: %s).";
		public static final String EVENT_IMAGE_DELETE_FAILED = "Failed to delete previous image for event ID: %s — Error: %s";
		public static final String EVENT_TEAMS_DELETED = "%d team(s) deleted with event '%s' (ID: %s).";
		public static final String EVENT_COMBOS_UPDATED = "%d combo(s) updated due to removal of event '%s' (ID: %s).";
		public static final String EVENT_COMBO_DELETED = "Combo ID: %s deleted — fewer than 2 events remaining after event removal.";

		// Event Combo messages
		public static final String COMBO_CREATED = "Combo '%s' (ID: %s) created by user: %s (ID: %s).";
		public static final String COMBO_UPDATED = "Combo '%s' (ID: %s) updated by user: %s (ID: %s).";
		public static final String COMBO_DELETED_LOG = "Combo '%s' (ID: %s) deleted by user: %s (ID: %s).";
		public static final String COMBO_RETRIEVED = "Combo '%s' (ID: %s) retrieved.";
		public static final String COMBOS_RETRIEVED = "%d event combo(s) retrieved.";
		public static final String COMBO_NOT_FOUND = "Event combo not found with ID: %s.";
		public static final String COMBO_REGISTRATION_TOGGLED = "Combo '%s' (ID: %s) registration toggled: %s -> %s by user: %s (ID: %s).";
		public static final String COMBO_IMAGE_UPDATED = "Combo '%s' (ID: %s) image updated. New fileId: %s. By user: %s (ID: %s).";
		public static final String COMBO_IMAGE_DELETE_FAILED = "Failed to delete previous image for combo ID: %s — Error: %s";
		public static final String COMBO_EVENT_ADDED = "Event '%s' (ID: %s) added to combo '%s' (ID: %s) by user: %s (ID: %s).";
		public static final String COMBO_EVENT_REMOVED = "Event '%s' (ID: %s) removed from combo '%s' (ID: %s) by user: %s (ID: %s).";
		public static final String COMBO_DOMAIN_MISMATCH = "Domain mismatch: event domain '%s' != combo domain '%s'. Cannot add event ID: %s to combo ID: %s.";
		public static final String COMBO_MIN_SIZE_VIOLATED = "Cannot remove event ID: %s from combo ID: %s — combo would fall below minimum of 2 events.";

		// User profile messages
		public static final String PROFILE_CREATED = "Profile created for user: %s (ID: %d)";
		public static final String PROFILE_UPDATED = "Profile updated for user: %s (ID: %d)";
		public static final String PROFILE_FETCHED = "Profile retrieved for user: %s (ID: %d)";
		public static final String PROFILES_LISTED = "Successfully retrieved %d profiles. Filter: isProfileCreated = %s";
		public static final String IDOR_ATTEMPT = "IDOR Attempt: User %s (ID: %d) tried to %s profile of %s (ID: %s)";
	}

	public static final class ToastMessage {
		private ToastMessage () {
		}

		public static final String REGISTRATION_SUCCESSFUL = "Registration successful.";
		public static final String LOGIN_SUCCESSFUL = "Login successful.";
		public static final String LOGOUT_SUCCESSFUL = "Logout successful.";
		public static final String OTP_VERIFIED = "OTP verified successfully.";
		public static final String PASSWORD_RESET_SUCCESSFUL = "Password reset successful.";
	}

	/**
	 * Email content related constants
	 */
	public static final class EmailConstants {
		private EmailConstants () {
		}

		// Email subjects and content
		public static final String MEGATRONIX = "Megatronix";
		public static final String EMAIL_SUBJECT_PREFIX = "Paridhi '26";
		public static final String RECIPIENTS = " recipients";
		public static final String RECIPIENTS_FOR_EVENT = " recipients for event: ";
		public static final String TEAM = ", team: ";

		// Email error messages
		public static final String ERROR = " - Error: ";
		public static final String RECIPIENTS_ERROR = " recipients - Error: ";
	}
}