package com.megatronix.paridhi.constant;

/**
 * Application-wide constants isUsed across the system.
 * Organized by functional categories for better readability and maintenance.
 */
public class AppConstant {
	private AppConstant () {
		// Private constructor to prevent instantiation
	}

	/**
	 * Service identifiers for logging
	 */
	public static final String MRD_SERVICE = "MRDService";
	public static final String OTP_SERVICE = "OtpService";
	public static final String EMAIL_SERVICE = "EmailService";
	public static final String CLOUDINARY_SERVICE = "CloudinaryService";
	public static final String IMAGEKIT_SERVICE = "ImageKitService";
	public static final String PASSWORD_RESET_SERVICE = "PasswordResetService";

	/**
	 * Designation categories for team members
	 */
	public static final String MEMBER = "Member";
	public static final String MEGATRON = "Megatron";
	public static final String APP_DEVELOPER = "App Developer";
	public static final String BACKEND_DEVELOPER = "Backend Developer";
	public static final String FRONTEND_DEVELOPER = "Frontend Developer";
	public static final String FULL_STACK_DEVELOPER = "Full Stack Developer";
	public static final String BACKEND_DEVELOPER_AND_APP_DEVELOPER = "Backend Dev & App Dev";

	/**
	 * Team categories for club members
	 */
	public static final String MEGATRONS = "megatrons";
	public static final String DEVELOPERS = "developers";

	/**
	 * Resource types for domain entities
	 */
	public static final String MRD = "MRD";
	public static final String USER = "User";
	public static final String TEAM = "Team";
	public static final String ADMIN = "Admin";
	public static final String BLACKLISTED_TOKEN = "BLACKLISTED_TOKEN";
	public static final String MAIL = "MAIL";
	public static final String EVENT = "Event";
	public static final String COMBO = "Combo";
	public static final String OTP = "Otp Token";
	public static final String PASSWORD_RESET = "Password Reset Token";
	public static final String PROFILE = "Profile";

	/**
	 * Resource types for media and images
	 */
	public static final String EVENT_IMAGE = "EventImage";
	public static final String COMBO_IMAGE = "ComboImage";
	public static final String IMAGE_STORAGE = "ImageStorage";
	public static final String CLOUDINARY_IMAGE = "CloudinaryImage";
	public static final String IMAGEKIT_IMAGE = "ImageKitImage";
	public static final String EVENT_COMBO = "EventCombo";

	/**
	 * Cloudinary related constants
	 */
	public static final String PUBLIC_ID = "public_id";
	public static final String SECURE_URL = "secure_url";
	public static final String CLOUDINARY_RESOURCE_TYPE = "resource_type";
	public static final String IMAGE = "image";
	public static final String FOLDER_KEY = "folder";
	public static final String OVERWRITE_KEY = "overwrite";
	public static final String INVALIDATE_KEY = "invalidate";
	public static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024; // 10 MB

	/**
	 * Request and logging attribute names
	 */
	public static final String REQUEST_ID = "requestId";
	public static final String CLIENT_IP = "clientIp";
	public static final String RESOURCE_TYPE = "resourceType";

	/**
	 * Special values and placeholders
	 */
	public static final String ANONYMOUS = "anonymous";
	public static final String UNKNOWN = "unknown";
	public static final String DEFAULT_EVENT_POSTER_URL = "https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/paridhi-2025-domain-poster.jpg";
	public static final String DEFAULT_EVENT_POSTER_FILE_ID = "69e0d63e5c7cd75eb89d8476";
	public static final String DEFAULT_PROFILE_PIC = "https://cdn-icons-png.flaticon.com/512/5951/5951752.png";

	/**
	 * Status indicators and codes
	 */
	public static final String FAILED = "FAILED";
	public static final String SUCCESS = "SUCCESS";
	public static final String OPEN = "OPEN";
	public static final String CLOSED = "CLOSED";

	/**
	 * Security related constants
	 */
	public static final String BEARER = "Bearer ";
	public static final String ACCESS_DENIED = "access_denied";
	public static final String ACCESS_GRANTED = "access_granted";

	/**
	 * UI/Email style constants
	 */
	public static final String PRIMARY_COLOR = "#1562D4"; // Blue
	public static final String SECONDARY_COLOR = "#64C882"; // Green
	public static final String BACKGROUND_COLOR = "#222222"; // Dark gray
	public static final String TEXT_COLOR = "#CCCCCC"; // Light gray
	public static final String H3_TEXT_COLOR = "#66c2ff"; // Light blue
	public static final String BORDER_BOTTOM_COLOR = "#0040ff"; // Blue
	public static final String H_COLOR = "#FFFFFF"; // White
	public static final String HEADER_BACKGROUND = "#1A1A1A"; // Even darker gray
	public static final String FIRST_PLACE_COLOR = "#FFD700"; // Gold
	public static final String SECOND_PLACE_COLOR = "#C0C0C0"; // Silver
	public static final String THIRD_PLACE_COLOR = "#CD7F32"; // Bronze
}