package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.Role;
import com.megatronix.paridhi.dto.request.ProfileRequest;
import com.megatronix.paridhi.dto.request.ProfileUpdateRequest;
import com.megatronix.paridhi.dto.response.ProfileResponse;
import com.megatronix.paridhi.dto.response.UserResponse;
import com.megatronix.paridhi.exception.ForbiddenAccessException;
import com.megatronix.paridhi.exception.ProfileAlreadyExistsException;
import com.megatronix.paridhi.exception.ProfileNotYetCreatedException;
import com.megatronix.paridhi.exception.UserNotFoundException;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.megatronix.paridhi.constant.AppConstant.Common.*;
import static com.megatronix.paridhi.constant.AppConstant.Resource.PROFILE;
import static com.megatronix.paridhi.constant.AppConstant.Resource.USER;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.*;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.ACCESS_DENIED;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.CONFLICT_ERROR;
import static com.megatronix.paridhi.util.RandomUtil.getRandomProfilePic;
import static java.lang.String.format;

@Service
public class ProfileService {

	private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
	private final UserRepository userRepository;

	public ProfileService ( UserRepository userRepository ) {
		this.userRepository = userRepository;
	}

	@Transactional
	public ProfileResponse createProfile ( ProfileRequest request, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to create profile for email: {}", request.email());
		}

		// step-1: security — prevent user enumeration or impersonation by verifying email ownership
		verifyOwnership(user, request.email());

		// step-2: fetch the USER from the DB
		User existingUser = fetchUserByEmail(request.email(), user);

		// step-3: guard — ensure the account is email-verified before allowing profile creation
		if ( ! existingUser.isVerified() ) {
			LoggingUtil.logSecurity(log, CREATE, user, FAILED, format(USER_UNVERIFIED, existingUser.getEmail()));
			throw new ForbiddenAccessException("User email must be verified to create a profile");
		}

		// step-4: guard — prevent duplicate profile creation
		if ( existingUser.isProfileCreated() ) {
			LoggingUtil.logError(log, CREATE, PROFILE, user, format(PROFILE_ALREADY_EXISTS, existingUser.getEmail()), null);
			throw new ProfileAlreadyExistsException(CONFLICT_ERROR);
		}

		// step-5: populate profile details and mark as created
		existingUser.setPhoneNumber(request.phoneNumber());
		existingUser.setCollege(request.college());
		existingUser.setYear(request.year());
		existingUser.setDepartment(request.department());
		existingUser.setRollNo(request.rollNo());
		existingUser.setProfilePictureUrl(getRandomProfilePic());
		existingUser.setProfileCreated(true);

		// step-6: audit log the successful profile creation
		LoggingUtil.logOperation(log, CREATE, PROFILE, user, format(PROFILE_CREATED, existingUser.getEmail(), existingUser.getUserId()));

		return ProfileResponse.builder()
			.profileDetails(UserResponse.fromUser(existingUser))
			.build();
	}

	@Transactional ( readOnly = true )
	public ProfileResponse getProfileById ( Long id, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to fetch profile for user ID: {}", id);
		}

		// step-1: security — prevent IDOR by verifying that the requesting user owns the ID or is an ADMIN
		verifyOwnership(user, id, READ);

		// step-2: fetch the USER and verify they exist
		User existingUser = fetchUserById(id, user, READ);

		// step-3: audit log the successful retrieval
		LoggingUtil.logOperation(log, READ, PROFILE, user, format(PROFILE_FETCHED, existingUser.getEmail(), existingUser.getUserId()));

		return ProfileResponse.builder()
			.profileDetails(UserResponse.fromUser(existingUser))
			.build();
	}

	@Transactional ( readOnly = true )
	public List< ProfileResponse > getAllByIsProfileCreated ( boolean isProfileCreated, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to list profiles with status: {}", isProfileCreated);
		}

		// step-1: security — explicit ADMIN check to prevent regular users from listing all profiles
		if ( user.getRole() == Role.ROLE_USER ) {
			LoggingUtil.logSecurity(log, AppConstant.Security.ACCESS_DENIED, user, FAILED, "Unauthorized attempt to list all profiles");
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}

		// step-2: retrieve filtered profiles from the repository
		List< User > users = userRepository.findAllByIsProfileCreated(isProfileCreated);

		// step-3: audit log the list operation
		LoggingUtil.logOperation(log, READ, PROFILE, user, format(PROFILES_LISTED, users.size(), isProfileCreated));

		return users.stream()
			.map(existingUser -> ProfileResponse.builder()
				.profileDetails(UserResponse.fromUser(existingUser))
				.build()
			).toList();
	}

	@Transactional
	public ProfileResponse updateProfile ( Long id, ProfileUpdateRequest request, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update profile for user ID: {}", id);
		}

		// step-1: security — prevent IDOR by verifying ownership or ADMIN permissions
		verifyOwnership(user, id, UPDATE);

		// step-2: fetch the USER from the DB
		User existingUser = fetchUserById(id, user, UPDATE);

		// step-3: guard — ensure the user actually has a profile to update
		if ( ! existingUser.isProfileCreated() ) {
			LoggingUtil.logError(log, UPDATE, PROFILE, user, format(PROFILE_NOT_CREATED, existingUser.getEmail()), null);
			throw new ProfileNotYetCreatedException();
		}

		// step-4: apply updates to profile fields
		existingUser.setPhoneNumber(request.phoneNumber());
		existingUser.setCollege(request.college());
		existingUser.setYear(request.year());
		existingUser.setDepartment(request.department());
		existingUser.setRollNo(request.rollNo());

		// step-5: audit log the successful update
		LoggingUtil.logOperation(log, UPDATE, PROFILE, user, format(PROFILE_UPDATED, existingUser.getEmail(), existingUser.getUserId()));

		return ProfileResponse.builder()
			.profileDetails(UserResponse.fromUser(existingUser))
			.build();
	}

	/*
	 * private methods — used internally only
	 */

	private void verifyOwnership ( User authUser, Long targetId, String operation ) {
		if ( authUser == null ) throw new ForbiddenAccessException(ACCESS_DENIED);

		// allow ADMIN or the owner of the ID
		if ( authUser.getRole() == Role.ROLE_USER && ! authUser.getUserId().equals(targetId) ) {
			LoggingUtil.logSecurity(log, AppConstant.Security.ACCESS_DENIED, authUser, FAILED, format(IDOR_ATTEMPT, authUser.getEmail(), authUser.getUserId(), operation, "another user", targetId));
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}
	}

	private void verifyOwnership ( User authUser, String targetEmail ) {
		if ( authUser == null ) throw new ForbiddenAccessException(ACCESS_DENIED);

		// allow ADMIN or the owner of the email
		if ( authUser.getRole() == Role.ROLE_USER && ! authUser.getEmail().equals(targetEmail) ) {
			LoggingUtil.logSecurity(log, AppConstant.Security.ACCESS_DENIED, authUser, FAILED, format(IDOR_ATTEMPT, authUser.getEmail(), authUser.getUserId(), CREATE, targetEmail, "N/A"));
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}
	}

	private User fetchUserById ( Long id, User authUser, String operation ) {
		return userRepository.findById(id).orElseThrow(() -> {
			LoggingUtil.logError(log, operation, PROFILE, authUser, format(NOT_FOUND_LOG, USER, "ID", id), null);
			return new UserNotFoundException(format(NOT_FOUND, USER));
		});
	}

	private User fetchUserByEmail ( String email, User authUser ) {
		return userRepository.findUserByEmail(email).orElseThrow(() -> {
			LoggingUtil.logError(log, CREATE, PROFILE, authUser, format(EMAIL_NOT_FOUND, email), null);
			return new UserNotFoundException(format(NOT_FOUND, USER));
		});
	}
}