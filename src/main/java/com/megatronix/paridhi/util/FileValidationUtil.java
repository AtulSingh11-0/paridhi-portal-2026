package com.megatronix.paridhi.util;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.exception.InvalidFileException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class FileValidationUtil {

	private FileValidationUtil() {
		// Private constructor to prevent instantiation
	}

	private static final Set< String > VALID_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/jpg", "image/gif",
		"image/webp", "image/svg+xml", "image/bmp", "image/tiff", "image/x-icon", "image/heic", "image/heif", "image/avif");

	public static void validateImage ( MultipartFile file ) {
		if ( file == null || file.isEmpty() ) {
			throw new InvalidFileException("File cannot be null or empty");
		}

		if ( file.getSize() > AppConstant.Storage.MAX_IMAGE_SIZE ) {
			throw new InvalidFileException("File size exceeds the maximum limit of " + ( AppConstant.Storage.MAX_IMAGE_SIZE / ( 1024 * 1024 ) ) + " MB");
		}

		String contentType = file.getContentType();
		if ( contentType == null || ( ! contentType.startsWith("image/") && ! VALID_IMAGE_TYPES.contains(contentType) ) ) {
			throw new InvalidFileException(
				String.format("Invalid image format: %s. Allowed formats: JPEG, PNG, GIF, WebP, SVG, HEIC, HEIF, TIFF, BMP", contentType)
			);
		}
	}

	public static String sanitizeFileName ( String originalFileName ) {
		if ( originalFileName == null || originalFileName.trim().isEmpty() ) {
			return "unnamed_file";
		}

		String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.\\-]", "_")
			.replaceAll("\\.{2,}", ".")
			.replaceAll("_+", "_")
			.replaceAll("(^[_.-]+)|([_.-]+$)", "")
			.toLowerCase();

		return sanitizedFileName.isEmpty() ? "unnamed_file" : sanitizedFileName;
	}
}
