package com.megatronix.paridhi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.dto.response.ImageUploadResult;
import com.megatronix.paridhi.exception.FileUploadException;
import com.megatronix.paridhi.util.FileValidationUtil;
import com.megatronix.paridhi.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Service for handling file uploads and deletions with Cloudinary
 */
@Service
public class CloudinaryService implements ImageStorageService {
	private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
	private final Cloudinary cloudinary;

	public CloudinaryService ( Cloudinary cloudinary ) {
		this.cloudinary = cloudinary;
	}

	@Value ( "${image-storage.folder.base:paridhi-2026/}" )
	private String baseFolder;

	/**
	 * Upload a file to Cloudinary
	 *
	 * @param file The file to upload
	 *
	 * @return Map containing the secure URL and public ID of the uploaded file
	 *
	 * @throws FileUploadException if the upload fails
	 */
	@Override
	public ImageUploadResult uploadFile ( MultipartFile file, String folderName ) {
		// 1. validate and sanitize file
		FileValidationUtil.validateImage(file);
		String safeFileName = FileValidationUtil.sanitizeFileName(file.getOriginalFilename());
		String targetFolder = baseFolder + "/" + ( folderName != null ? folderName : "others" );
		String publicId = safeFileName + "_" + LocalDate.now() + "_" + UUID.randomUUID().toString().substring(0, 8);

		// log the upload operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			AppConstant.Service.CLOUDINARY,
			null,
			String.format("Uploading file: %s (%d bytes) to folder: %s", safeFileName, file.getSize(), targetFolder)
		);

		try {
			// upload the file to cloudinary
			Map< ?, ? > uploadResult = cloudinary.uploader()
				.upload(
					file.getBytes(),
					ObjectUtils.asMap(
						AppConstant.Storage.PUBLIC_ID, publicId,
						AppConstant.Storage.RESOURCE_TYPE, AppConstant.Storage.IMAGE,
						AppConstant.Storage.FOLDER, targetFolder,
						AppConstant.Storage.OVERWRITE, true
					)
				);

			// get the secure URL of the uploaded file
			String secureUrl = (String) uploadResult.get(AppConstant.Storage.SECURE_URL);
			String fullPublicId = targetFolder + "/" + publicId;

			// log the successful upload
			LoggingUtil.logOperation(
				log,
				MessageConstant.Operation.CREATE,
				AppConstant.Service.CLOUDINARY,
				null,
				String.format(MessageConstant.SuccessTemplate.CREATED, AppConstant.Storage.CLOUDINARY_IMAGE, fullPublicId)
			);

			// 3. return standardized DTO
			return new ImageUploadResult(fullPublicId, secureUrl);
		} catch ( IOException e ) {
			// log the error
			LoggingUtil.logError(
				log,
				MessageConstant.Operation.CREATE,
				AppConstant.Service.CLOUDINARY,
				null,
				String.format(MessageConstant.ErrorTemplate.OPERATION_FAILED, "upload file", file.getOriginalFilename(), e.getMessage()),
				e
			);
			throw new FileUploadException(MessageConstant.UserMessage.FILE_UPLOAD_ERROR, e);
		}
	}

	/**
	 * Delete a file from Cloudinary using its public ID
	 *
	 * @param fileId The public ID of the file to delete
	 *
	 * @throws FileUploadException if the deletion fails
	 */
	@Override
	public void deleteFile ( String fileId ) {
		if ( fileId == null || fileId.trim().isEmpty() ) return;

		// log the delete operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.DELETE,
			AppConstant.Service.CLOUDINARY,
			null,
			String.format("Deleting file from Cloudinary with public ID: %s", fileId)
		);

		try {
			// delete the file from cloudinary
			cloudinary.uploader()
				.destroy(
					fileId,
					ObjectUtils.asMap(
						AppConstant.Storage.RESOURCE_TYPE, AppConstant.Storage.IMAGE,
						AppConstant.Storage.INVALIDATE, true
					)
				);

			// log the successful deletion
			LoggingUtil.logOperation(
				log,
				MessageConstant.Operation.DELETE,
				AppConstant.Service.CLOUDINARY,
				null,
				String.format(MessageConstant.SuccessTemplate.DELETED, AppConstant.Storage.CLOUDINARY_IMAGE, fileId)
			);
		} catch ( IOException e ) {
			// log the error
			LoggingUtil.logError(
				log,
				MessageConstant.Operation.DELETE,
				AppConstant.Service.CLOUDINARY,
				null,
				String.format(MessageConstant.ErrorTemplate.OPERATION_FAILED, "delete file", fileId, e.getMessage()),
				e
			);
			throw new FileUploadException(MessageConstant.UserMessage.FILE_DELETION_ERROR, e);
		}
	}
}