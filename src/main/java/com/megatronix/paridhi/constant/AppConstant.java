package com.megatronix.paridhi.constant;

/**
 * Application-wide technical constants used across the system.
 * Organized into inner classes by functional domain for clarity and maintenance.
 */
public final class AppConstant {

	private AppConstant () {
		// Private constructor to prevent instantiation
	}

	/**
	 * Service identifiers for audit logging and service discovery.
	 */
	public static final class Service {
		private Service () {
		}

		public static final String MRD = "MRDService";
		public static final String OTP = "OtpService";
		public static final String EMAIL = "EmailService";
		public static final String CLOUDINARY = "CloudinaryService";
		public static final String IMAGEKIT = "ImageKitService";
		public static final String PASSWORD_RESET = "PasswordResetService";
		public static final String CRD = "CRDService";
	}

	/**
	 * Domain entity identifiers for logging and mapping.
	 */
	public static final class Resource {
		private Resource () {
		}

		public static final String EMAIL = "email";
		public static final String MRD = "MRD";
		public static final String USER = "User";
		public static final String TEAM = "Team";
		public static final String ADMIN = "Admin";
		public static final String EVENT = "Event";
		public static final String COMBO = "Combo";
		public static final String PROFILE = "Profile";
		public static final String OTP = "Otp Token";
		public static final String PASSWORD_RESET = "Password Reset Token";
		public static final String BLACKLISTED_TOKEN = "BLACKLISTED_TOKEN";
	}

	/**
	 * Media and image storage related constants.
	 */
	public static final class Storage {
		private Storage () {
		}

		public static final String EVENT_IMAGE = "EventImage";
		public static final String COMBO_IMAGE = "ComboImage";
		public static final String IMAGE_STORAGE = "ImageStorage";
		public static final String CLOUDINARY_IMAGE = "CloudinaryImage";
		public static final String IMAGEKIT_IMAGE = "ImageKitImage";

		public static final String PUBLIC_ID = "public_id";
		public static final String SECURE_URL = "secure_url";
		public static final String RESOURCE_TYPE = "resource_type";
		public static final String IMAGE = "image";
		public static final String FOLDER = "folder";
		public static final String OVERWRITE = "overwrite";
		public static final String INVALIDATE = "invalidate";

		public static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024; // 10 MB

		public static final String DEFAULT_EVENT_POSTER_URL = "https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/paridhi-2025-domain-poster.jpg";
		public static final String DEFAULT_EVENT_POSTER_FILE_ID = "69e0d63e5c7cd75eb89d8476";
		public static final String DEFAULT_PROFILE_PIC = "https://cdn-icons-png.flaticon.com/512/5951/5951752.png";
	}

	/**
	 * Security, identity, and request tracking constants.
	 */
	public static final class Security {
		private Security () {
		}

		public static final String REQUEST_ID = "requestId";
		public static final String CLIENT_IP = "clientIp";
		public static final String RESOURCE_TYPE = "resourceType";
		public static final String VISITOR_COOKIE = "Paridhi-Visitor-Id";
		public static final String USER_AGENT = "User-Agent";
		public static final String X_FORWARDED_FOR = "X-Forwarded-For";

		public static final String ANONYMOUS = "anonymous";
		public static final String ANONYMOUS_USER = "anonymousUser";
		public static final String UNKNOWN = "unknown";
		public static final String BEARER = "Bearer ";

		public static final String ACCESS_DENIED = "access_denied";

		public static final String USER_PREFIX = "user:";
		public static final String VISITOR_PREFIX = "visitor:";
		public static final String NET_PREFIX = "net:";
	}

	/**
	 * Visual styling constants for emails and UI.
	 */
	public static final class Style {
		private Style () {
		}

		public static final String PRIMARY_COLOR = "#1562D4";
		public static final String SECONDARY_COLOR = "#64C882";
		public static final String BACKGROUND_COLOR = "#222222";
		public static final String TEXT_COLOR = "#CCCCCC";
		public static final String H3_TEXT_COLOR = "#66c2ff";
		public static final String BORDER_BOTTOM_COLOR = "#0040ff";
		public static final String H_COLOR = "#FFFFFF";
		public static final String HEADER_BACKGROUND = "#1A1A1A";

		public static final String FIRST_PLACE_COLOR = "#FFD700";
		public static final String SECOND_PLACE_COLOR = "#C0C0C0";
		public static final String THIRD_PLACE_COLOR = "#CD7F32";
	}

	/**
	 * Generic status and encoding constants.
	 */
	public static final class Common {
		private Common () {
		}

		public static final String FAILED = "FAILED";
		public static final String SUCCESS = "SUCCESS";
		public static final String OPEN = "OPEN";
		public static final String CLOSED = "CLOSED";
		public static final String UTF_8 = "UTF-8";
	}
}