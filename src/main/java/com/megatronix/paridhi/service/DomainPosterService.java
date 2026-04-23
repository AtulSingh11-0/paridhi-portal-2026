package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.constant.Role;
import com.megatronix.paridhi.dto.response.DomainPosterAdminResponse;
import com.megatronix.paridhi.dto.response.DomainPosterPublicResponse;
import com.megatronix.paridhi.dto.response.ImageUploadResult;
import com.megatronix.paridhi.exception.DomainPosterNotFoundException;
import com.megatronix.paridhi.exception.ForbiddenAccessException;
import com.megatronix.paridhi.model.DomainPoster;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.DomainPosterRepository;
import com.megatronix.paridhi.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DomainPosterService {
	private static final Logger log = LoggerFactory.getLogger(DomainPosterService.class);
	private final ImageStorageService imageStorageService;
	private final DomainPosterRepository domainPosterRepository;

	public DomainPosterService (
		ImageStorageService imageStorageService,
		DomainPosterRepository domainPosterRepository
	) {
		this.imageStorageService = imageStorageService;
		this.domainPosterRepository = domainPosterRepository;
	}

	/*
	 * Public methods - doesn't require authentication
	 */

	public DomainPosterPublicResponse getDomainPosterByDomain ( Domain domain ) {
		LoggingUtil.logOperation(log, MessageConstant.Operation.READ, AppConstant.DOMAIN_POSTER_SERVICE, null, "Fetching domain poster for domain: " + domain);

		DomainPoster poster = domainPosterRepository.findByDomain(domain)
			.orElseThrow(() -> {
				LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.DOMAIN_POSTER_SERVICE, null, String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.DOMAIN_POSTER), null);
				return new DomainPosterNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.DOMAIN_POSTER));
			});

		LoggingUtil.logOperation(log, MessageConstant.Operation.READ, AppConstant.DOMAIN_POSTER_SERVICE, null, String.format(MessageConstant.SuccessTemplate.FETCHED, "domain poster for domain: " + domain));

		return DomainPosterPublicResponse.fromDomainPoster(poster);
	}

	public List< DomainPosterPublicResponse > getAllDomainPosters () {
		LoggingUtil.logOperation(log, MessageConstant.Operation.READ, AppConstant.DOMAIN_POSTER_SERVICE, null, "Fetching all domain posters");

		var existingDomainPosters = domainPosterRepository.findAll();
		if ( existingDomainPosters.isEmpty() ) {
			LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.DOMAIN_POSTER_SERVICE, null, "No domain posters found", null);
			throw new DomainPosterNotFoundException("No domain posters found");
		}

		LoggingUtil.logOperation(log, MessageConstant.Operation.READ, AppConstant.DOMAIN_POSTER_SERVICE, null, String.format(MessageConstant.SuccessTemplate.FETCHED_COUNT, existingDomainPosters.size(), "domain posters"));

		return existingDomainPosters.stream()
			.map(DomainPosterPublicResponse::fromDomainPoster)
			.toList();
	}

	/*
	 * Protected methods - requires authentication
	 */

	@Transactional
	public DomainPosterAdminResponse saveDomainPoster ( Domain domain, MultipartFile file, User user ) {
		LoggingUtil.logOperation(log, MessageConstant.Operation.CREATE, AppConstant.DOMAIN_POSTER_SERVICE, user, "Creating domain poster for domain: " + domain);
		checkUserAccess(user, MessageConstant.Operation.CREATE);

		var existingPoster = domainPosterRepository.findByDomain(domain);

		if ( existingPoster.isPresent() ) {
			LoggingUtil.logOperation(log, MessageConstant.Operation.UPDATE, AppConstant.DOMAIN_POSTER_SERVICE, user, "Found existing poster for domain: " + domain + ", updating instead of creating new one");
			return handlePosterUpdate(existingPoster.get(), domain, file, user);
		}

		// upload domain poster
		ImageUploadResult result = imageStorageService.uploadFile(file, AppConstant.DOMAIN_POSTER);

		var domainPoster = DomainPoster.builder()
			.domain(domain)
			.domainPosterUrl(result.fileUrl())
			.domainPosterFileId(result.fileId())
			.build();

		var savedDomainPoster = domainPosterRepository.save(domainPoster);

		LoggingUtil.logOperation(log, MessageConstant.Operation.CREATE, AppConstant.DOMAIN_POSTER_SERVICE, user, String.format(MessageConstant.SuccessTemplate.CREATED, AppConstant.DOMAIN_POSTER, savedDomainPoster.getDomainPosterId()) + AppConstant.FOR_DOMAIN + domain);

		return DomainPosterAdminResponse.fromDomainPoster(savedDomainPoster);
	}

	@Transactional
	public DomainPosterAdminResponse updateDomainPoster ( Long id, Domain domain, MultipartFile file, User user ) {
		LoggingUtil.logOperation(log, MessageConstant.Operation.UPDATE, AppConstant.DOMAIN_POSTER_SERVICE, user, "Updating domain poster with ID: " + id + AppConstant.FOR_DOMAIN + domain);
		checkUserAccess(user, MessageConstant.Operation.UPDATE);

		var existingPoster = fetchDomainPosterById(id, user);

		return handlePosterUpdate(existingPoster, domain, file, user);
	}

	@Transactional
	public void deleteDomainPoster ( Long id, User user ) {
		LoggingUtil.logOperation(log, MessageConstant.Operation.DELETE, AppConstant.DOMAIN_POSTER_SERVICE, user, "Deleting domain poster with ID: " + id);
		checkUserAccess(user, MessageConstant.Operation.DELETE);

		var existingPoster = fetchDomainPosterById(id, user);

		if ( existingPoster.getDomainPosterUrl() != null && existingPoster.getDomainPosterFileId() != null ) {
			try {
				imageStorageService.deleteFile(existingPoster.getDomainPosterFileId());
			} catch ( Exception e ) {
				LoggingUtil.logError(log, MessageConstant.Operation.DELETE, AppConstant.IMAGE_STORAGE, user, "Failed to delete image for poster ID: " + id + " - Continuing with deletion", e);
			}
		}

		domainPosterRepository.delete(existingPoster);

		LoggingUtil.logOperation(log, MessageConstant.Operation.DELETE, AppConstant.DOMAIN_POSTER_SERVICE, user, String.format(MessageConstant.SuccessTemplate.DELETED, AppConstant.DOMAIN_POSTER, id) + AppConstant.FOR_DOMAIN + existingPoster.getDomain());
	}

	/*
	 * Private methods - isUsed internally
	 */

	private DomainPosterAdminResponse handlePosterUpdate ( DomainPoster existingPoster, Domain domain, MultipartFile file, User user ) {
		if ( existingPoster.getDomainPosterUrl() != null && existingPoster.getDomainPosterFileId() != null ) {
			try {
				imageStorageService.deleteFile(existingPoster.getDomainPosterFileId());
			} catch ( Exception e ) {
				LoggingUtil.logError(log, MessageConstant.Operation.DELETE, AppConstant.IMAGE_STORAGE, user, "Failed to delete previous image for poster - Error: " + e.getMessage(), e);
			}
		}

		ImageUploadResult result = imageStorageService.uploadFile(file, AppConstant.DOMAIN_POSTER);

		existingPoster.setDomain(domain);
		existingPoster.setDomainPosterUrl(result.fileUrl());
		existingPoster.setDomainPosterFileId(result.fileId());

		LoggingUtil.logOperation(log, MessageConstant.Operation.UPDATE, AppConstant.DOMAIN_POSTER_SERVICE, user, String.format(MessageConstant.SuccessTemplate.UPDATED, AppConstant.DOMAIN_POSTER, existingPoster.getDomainPosterId()) + AppConstant.FOR_DOMAIN + domain);

		return DomainPosterAdminResponse.fromDomainPoster(existingPoster);
	}

	private void checkUserAccess ( User user, String operation ) {
		if ( user == null ) {
			LoggingUtil.logSecurity(log, AppConstant.ACCESS_DENIED, null, AppConstant.FAILED, String.format(MessageConstant.ErrorTemplate.AUTHENTICATION_REQUIRED, operation + " " + AppConstant.DOMAIN_POSTER));
			throw new ForbiddenAccessException(MessageConstant.UserMessage.ACCESS_DENIED);
		}

		if ( user.getRole().equals(Role.ROLE_USER) ) {
			LoggingUtil.logSecurity(log, AppConstant.ACCESS_DENIED, user, AppConstant.FAILED, String.format(MessageConstant.ErrorTemplate.NOT_AUTHORIZED, operation + " " + AppConstant.DOMAIN_POSTER));
			throw new ForbiddenAccessException(MessageConstant.UserMessage.ACCESS_DENIED);
		}

		LoggingUtil.logSecurity(log, AppConstant.ACCESS_GRANTED, user, AppConstant.SUCCESS, String.format(MessageConstant.SuccessTemplate.AUTHORIZED, operation + " " + AppConstant.DOMAIN_POSTER));
	}

	private DomainPoster fetchDomainPosterById ( Long id, User user ) {
		return domainPosterRepository.findById(id)
			.orElseThrow(() -> {
				LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.DOMAIN_POSTER_SERVICE, user, String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.DOMAIN_POSTER), null);
				return new DomainPosterNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.DOMAIN_POSTER));
			});
	}
}