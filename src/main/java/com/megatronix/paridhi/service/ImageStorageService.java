package com.megatronix.paridhi.service;

import com.megatronix.paridhi.dto.response.ImageUploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
	ImageUploadResult uploadFile ( MultipartFile file, String folderName );

	void deleteFile ( String fileId );
}
