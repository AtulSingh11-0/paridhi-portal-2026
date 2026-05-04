package com.megatronix.paridhi.util;


import java.security.SecureRandom;
import java.util.List;

public final class RandomUtil {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final String UNAMBIGUOUS_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()" +
		"-_=+[]{}|;:,.<>?";
	private static final List< String > DEFAULT_PROFILE_PICS = List.of(
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/dexter-pfp.jpg?updatedAt=1777932003551",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/finn-pfp.jpg?updatedAt=1777932003604",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/bob-pfp.webp?updatedAt=1777932003740",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/jerry-pfp.png?updatedAt=1777932003640",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/tom-pfp.jpg?updatedAt=1777932003668",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/buttercup-pfp.webp?updatedAt=1777932003797",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/eustace-pfp.webp?updatedAt=1777932003800",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/bubbles-pfp.webp?updatedAt=1777932003828",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/muriel-pfp.jpg?updatedAt=1777932003732",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/marky-pfp.jpg?updatedAt=1777932003729",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/deedee-pfp.jpg?updatedAt=1777932003750",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/courage-pfp.jpg?updatedAt=1777932003732",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/ben-10-pfp.jpg?updatedAt=1777932003755",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/oggy-pfp.jpg?updatedAt=1777932003749",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/jack-pfp.jpg?updatedAt=1777932003861",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/johnny-bravo-pfp.jpg?updatedAt=1777932003868",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/wrath-pfp.jpg?updatedAt=1777932003774",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/mandy-pfp.jpg?updatedAt=1777932003789",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/billy-pfp.jpg?updatedAt=1777932003794",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/jake-pfp.jpg?updatedAt=1777932003800",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/freaky_fred-pfp.webp?updatedAt=1777932003952",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/blossom-pfp.png?updatedAt=1777932003824",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/grim-pfp.jpg?updatedAt=1777932003829",
		"https://ik.imagekit.io/vhp6ho6m1x/megatronix/paridhi-2026/pfp/joey-pfp.jpg?updatedAt=1777932003824"
	);

	private RandomUtil () {
	}

	public static SecureRandom getSecureRandom () {
		return SECURE_RANDOM;
	}

	public static int generateRandomInteger ( int min, int max ) {
		if ( min >= max ) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		return min + SECURE_RANDOM.nextInt(( max - min ) + 1);
	}

	public static String getRandomProfilePic () {
		int index = SECURE_RANDOM.nextInt(DEFAULT_PROFILE_PICS.size());
		return DEFAULT_PROFILE_PICS.get(index);
	}

	public static String generateOtp () {
		int otpNum = 100000 + SECURE_RANDOM.nextInt(900000); // 6-digit number between 100000 and 999999
		return String.valueOf(otpNum);
	}

	public static String generateResetToken ( int length ) {
		return generateRandomString(length, ALPHANUMERIC_CHARS);
	}

	public static String generateRandomPassword ( int length ) {
		return generateRandomString(length, CHAR_POOL);
	}

	public static String generateIdSalt ( int length ) {
		return generateRandomString(length, UNAMBIGUOUS_CHARS);
	}

	private static String generateRandomString ( int length, String charSet ) {
		if ( length <= 0 ) {
			throw new IllegalArgumentException("length must be positive");
		}

		StringBuilder result = new StringBuilder(length);
		for ( int i = 0; i < length; i++ ) {
			int index = SECURE_RANDOM.nextInt(charSet.length());
			result.append(charSet.charAt(index));
		}
		return result.toString();
	}
}
