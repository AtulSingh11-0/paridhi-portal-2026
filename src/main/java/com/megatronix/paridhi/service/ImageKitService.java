package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.dto.response.ImageUploadResult;
import com.megatronix.paridhi.exception.FileUploadException;
import com.megatronix.paridhi.util.FileValidationUtil;
import com.megatronix.paridhi.util.LoggingUtil;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
public class ImageKitService implements ImageStorageService {
	private static final Logger log = LoggerFactory.getLogger(ImageKitService.class);
	private final ImageKit imageKit;

	public ImageKitService ( ImageKit imageKit ) {
		this.imageKit = imageKit;
	}

	@Value ( "${image-storage.folder.base:paridhi-2026/}" )
	private String baseFolder;

	@Override
	public ImageUploadResult uploadFile ( MultipartFile file, String folderName ) {
		// 1. validate and sanitize file
		FileValidationUtil.validateImage(file);
		String safeFileName = FileValidationUtil.sanitizeFileName(file.getOriginalFilename());
		String targetFolder = baseFolder + folderName;
//		log.info("Uploading file [{}] to ImageKit folder [{}]", safeFileName, targetFolder);

		// log the upload operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			AppConstant.Service.IMAGEKIT,
			null,
			String.format("Uploading file: %s (%d bytes) to folder: %s", safeFileName, file.getSize(), targetFolder)
		);

		try {
			// 2. upload file to ImageKit
			FileCreateRequest request = new FileCreateRequest(file.getBytes(), file.getOriginalFilename());
			request.setFolder(targetFolder);
			Result result = imageKit.upload(request);
//			log.info("Successfully uploaded to ImageKit. FileId: {}", result.getFileId());

			// log the successful upload
			LoggingUtil.logOperation(
				log,
				MessageConstant.Operation.CREATE,
				AppConstant.Service.IMAGEKIT,
				null,
				String.format(MessageConstant.SuccessTemplate.CREATED, AppConstant.Storage.IMAGEKIT_IMAGE, result.getFileId())
			);

			// 3. return standardized DTO
			return new ImageUploadResult(result.getFileId(), result.getUrl());
		} catch ( Exception e ) {
			// log the error
			LoggingUtil.logError(
				log,
				MessageConstant.Operation.CREATE,
				AppConstant.Service.IMAGEKIT,
				null,
				String.format(MessageConstant.ErrorTemplate.OPERATION_FAILED, "upload file", safeFileName, e.getMessage()),
				e
			);
			throw new FileUploadException(MessageConstant.UserMessage.FILE_UPLOAD_ERROR, e);
		}
	}

	@Override
	public void deleteFile ( String fileId ) {
		if ( fileId == null || fileId.trim().isEmpty() ) return;
//		log.info("Deleting file from ImageKit with FileId: {}", fileId);
		// log the delete operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.DELETE,
			AppConstant.Service.IMAGEKIT,
			null,
			String.format("Deleting file from ImageKit with FileID: %s", fileId)
		);
		try {
			imageKit.deleteFile(fileId);
//			log.info("Successfully deleted file [{}] from ImageKit", fileId);
			// log the successful deletion
			LoggingUtil.logOperation(
				log,
				MessageConstant.Operation.DELETE,
				AppConstant.Service.IMAGEKIT,
				null,
				String.format(MessageConstant.SuccessTemplate.DELETED, AppConstant.Storage.IMAGEKIT_IMAGE, fileId)
			);
		} catch ( Exception e ) {
			// log the error
			LoggingUtil.logError(
				log,
				MessageConstant.Operation.DELETE,
				AppConstant.Service.IMAGEKIT,
				null,
				String.format(MessageConstant.ErrorTemplate.OPERATION_FAILED, "delete file", fileId, e.getMessage()),
				e
			);
			throw new FileUploadException(MessageConstant.UserMessage.FILE_DELETION_ERROR, e);
		}
	}
}
