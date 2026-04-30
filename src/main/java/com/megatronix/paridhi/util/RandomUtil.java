package com.megatronix.paridhi.util;


import java.security.SecureRandom;

public final class RandomUtil {
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final String UNAMBIGUOUS_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()" +
		"-_=+[]{}|;:,.<>?";

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

	public static String generateGidSalt ( int length ) {
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
