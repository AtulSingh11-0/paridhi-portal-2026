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

import static com.megatronix.paridhi.constant.AppConstant.*;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.EMAIL_NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.ACCESS_DENIED;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.CONFLICT_ERROR;

@Service
public class ProfileService {
	private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
	private final UserRepository userRepository;

	public ProfileService ( UserRepository userRepository ) {
		this.userRepository = userRepository;
	}

	@Transactional
	public ProfileResponse createProfile ( ProfileRequest request, User user ) {
		LoggingUtil.logOperation(log, CREATE, PROFILE, user, "Creating profile for email: " + request.email());

		// Security: Prevent User A from passing User B's email in the payload
		verifyOwnership(user, request.email());

		User existingUser = fetchUserByEmail(request.email(), user);

		if ( ! existingUser.isVerified() ) {
			LoggingUtil.logError(log, CREATE, PROFILE, user, "Unverified user ID: " + existingUser.getUserId(), null);
			throw new ForbiddenAccessException("User email must be verified to create a profile");
		}

		if ( existingUser.isProfileCreated() ) {
			LoggingUtil.logError(log, CREATE, PROFILE, user, CONFLICT_ERROR, null);
			throw new ProfileAlreadyExistsException(CONFLICT_ERROR);
		}

		existingUser.setPhoneNumber(request.phoneNumber());
		existingUser.setCollege(request.college());
		existingUser.setYear(request.year());
		existingUser.setDepartment(request.department());
		existingUser.setRollNo(request.rollNo());
		existingUser.setProfileCreated(true);

		LoggingUtil.logOperation(log, CREATE, PROFILE, user, "Profile created for user ID: " + existingUser.getUserId());

		return ProfileResponse.builder()
			.profileDetails(UserResponse.fromUser(existingUser))
			.build();
	}

	@Transactional ( readOnly = true )
	public ProfileResponse getProfileById ( Long id, User user ) {
		LoggingUtil.logOperation(log, READ, PROFILE, user, "Fetching profile for user ID: " + id);

		verifyOwnership(user, id, "view");
		User existingUser = fetchUserById(id, user, READ);

		LoggingUtil.logOperation(log, READ, PROFILE, user, "Profile retrieved for user ID: " + existingUser.getUserId());

		return ProfileResponse.builder()
			.profileDetails(UserResponse.fromUser(existingUser))
			.build();
	}

	@Transactional ( readOnly = true )
	public List< ProfileResponse > getAllByIsProfileCreated ( boolean isProfileCreated, User user ) {
		LoggingUtil.logOperation(log, READ, PROFILE, user, "Fetching profiles. Status: " + isProfileCreated);

		// Explicit Admin Check: Users should never list all profiles
		if ( user.getRole() == Role.ROLE_USER ) {
			LoggingUtil.logSecurity(log, AppConstant.ACCESS_DENIED, user, FAILED, "Attempted to list all profiles");
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}

		List< User > users = userRepository.findAllByIsProfileCreated(isProfileCreated);
		LoggingUtil.logOperation(log, READ, PROFILE, user, "Retrieved " + users.size() + " profiles");

		return users.stream()
			.map(existingUser -> ProfileResponse.builder()
				.profileDetails(UserResponse.fromUser(existingUser))
				.build()
			).toList();
	}

	@Transactional
	public ProfileResponse updateProfile ( Long id, ProfileUpdateRequest request, User user ) {
		LoggingUtil.logOperation(log, UPDATE, PROFILE, user, "Updating profile for user ID: " + id);

		verifyOwnership(user, id, "update");
		User existingUser = fetchUserById(id, user, UPDATE);

		if ( ! existingUser.isProfileCreated() ) {
			LoggingUtil.logError(log, UPDATE, PROFILE, user, "User " + existingUser.getUserId() + " has no profile", null);
			throw new ProfileNotYetCreatedException("User needs to create a profile first");
		}

		existingUser.setPhoneNumber(request.phoneNumber());
		existingUser.setCollege(request.college());
		existingUser.setYear(request.year());
		existingUser.setDepartment(request.department());
		existingUser.setRollNo(request.rollNo());

		LoggingUtil.logOperation(log, UPDATE, PROFILE, user, "Profile updated for user ID: " + existingUser.getUserId());

		return ProfileResponse.builder()
			.profileDetails(UserResponse.fromUser(existingUser))
			.build();
	}

	/*
	 * private methods - is used internally only
	 */

	private void verifyOwnership ( User authUser, Long targetId, String operation ) {
		if ( authUser == null ) throw new ForbiddenAccessException(ACCESS_DENIED);

		if ( authUser.getRole() == Role.ROLE_USER && ! authUser.getUserId().equals(targetId) ) {
			LoggingUtil.logSecurity(log, AppConstant.ACCESS_DENIED, authUser, FAILED, "IDOR Attempt: User tried to " + operation + " profile ID: " + targetId);
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}
	}

	private void verifyOwnership ( User authUser, String targetEmail ) {
		if ( authUser == null ) throw new ForbiddenAccessException(ACCESS_DENIED);

		if ( authUser.getRole() == Role.ROLE_USER && ! authUser.getEmail().equals(targetEmail) ) {
			LoggingUtil.logSecurity(log, AppConstant.ACCESS_DENIED, authUser, FAILED, "IDOR Attempt: User tried to create profile for profile for email: " + targetEmail);
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}
	}

	private User fetchUserById ( Long id, User authUser, String operation ) {
		return userRepository.findById(id).orElseThrow(() -> {
			LoggingUtil.logError(log, operation, PROFILE, authUser, String.format(NOT_FOUND, USER) + " ID: " + id, null);
			return new UserNotFoundException(String.format(NOT_FOUND, USER));
		});
	}

	private User fetchUserByEmail ( String email, User authUser ) {
		return userRepository.findUserByEmail(email).orElseThrow(() -> {
			LoggingUtil.logError(log, CREATE, PROFILE, authUser, String.format(EMAIL_NOT_FOUND, email), null);
			return new UserNotFoundException(String.format(NOT_FOUND, USER));
		});
	}
}