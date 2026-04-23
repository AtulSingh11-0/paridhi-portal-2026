package com.megatronix.paridhi.util;


import java.security.SecureRandom;

public final class RandomUtil {
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	private RandomUtil() {}

	public static SecureRandom getSecureRandom() {
		return SECURE_RANDOM;
	}

	public static int generateRandomInteger(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		return min + SECURE_RANDOM.nextInt((max - min) + 1);
	}

	public static String generateOtp() {
		int otpNum = 100000 + SECURE_RANDOM.nextInt(900000); // 6-digit number between 100000 and 999999
		return String.valueOf(otpNum);
	}

	public static String generateResetToken(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("length must be positive");
		}

		StringBuilder token = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = SECURE_RANDOM.nextInt(ALPHANUMERIC_CHARS.length());
			token.append(ALPHANUMERIC_CHARS.charAt(index));
		}
		return token.toString();
	}
}
