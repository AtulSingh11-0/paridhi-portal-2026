package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.constant.Role;
import com.megatronix.paridhi.dto.request.AdminMRDRequest;
import com.megatronix.paridhi.dto.response.MRDResponse;
import com.megatronix.paridhi.exception.*;
import com.megatronix.paridhi.model.MRD;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.MRDRepository;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.security.JwtService;
import com.megatronix.paridhi.util.LoggingUtil;
import com.megatronix.paridhi.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.megatronix.paridhi.constant.AppConstant.BEARER;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.INVALID_AUTH_HEADER;
import static com.megatronix.paridhi.dto.response.MRDResponse.from;

@Slf4j
@Service
@RequiredArgsConstructor
public class MRDService {
	private final JwtService jwtService;
	private final EmailService emailService;
	private final MRDRepository mrdRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final StringRedisTemplate stringRedisTemplate;
	private static final String PASSWORD = RandomUtil.generateRandomPassword(12);
	private static final String MRD_SEQ_KEY = "paridhi:mrd:sequence:2026";
	private static final String FETCHED = "Fetched ";
	private static final String MRD_SERVICE = "MRDService";
	private static final String GID_NOT_FOUND = "GID not found: ";
	private static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: ";

	private static MRDResponse apply ( MRD mrd ) {
		return from(mrd.getGid());
	}

	/*
	 * public methods - doesn't require authentication
	 */

	@Transactional
	public MRDResponse performUserMRD ( String authHeader ) {
		if ( authHeader == null || ! authHeader.startsWith(BEARER) ) {
			throw new ForbiddenAccessException(INVALID_AUTH_HEADER);
		}
		String token = authHeader.substring(7);
		String email = jwtService.extractUsername(token);

		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			MRD_SERVICE,
			null,
			"MRD registration request: " + email);

		// find the user
		var user = fetchUserByEmail(email);
		validateUserForRegistration(user);

		// create and save the MRD registration
		MRD savedMRD = createandSaveMRD(email);

		// send email to the user
		emailService.sendMRDWelcome(user.getEmail(), user.getName(), savedMRD.getGid());

		// return the response
		return from(savedMRD.getGid());
	}

	@Transactional
	public MRDResponse performUserMRDByAdmin ( AdminMRDRequest request ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			MRD_SERVICE,
			null,
			"Admin MRD registration request: " + request
		);

		// step 1: find the user from the user table, if he exists then use the previous data or else create a new user

		// find the user
		var existingUserOptional = userRepository.findUserByEmail(request.getEmail());

		// if the user already exists, then call registerMRD method and return the response
		if ( existingUserOptional.isPresent() ) {
			User existingUser = existingUserOptional.get();
			MRD savedMRD = createandSaveMRD(request.getEmail());
			emailService.sendMRDWelcome(existingUser.getEmail(), existingUser.getName(), savedMRD.getGid());
			return from(savedMRD.getGid());
		}

		// if the user doesn't exist, then create a new user and then call registerMRD method and return the response
		User newUser = createAndSaveUserByAdmin(request);
		MRD savedMRD = createandSaveMRD(request.getEmail());
		emailService.sendMRDWelcome(newUser.getEmail(), newUser.getName(), savedMRD.getGid());
		return from(savedMRD.getGid());
	}

	/*
	 * protected methods - requires authentication
	 */

	@Transactional
	public MRDResponse updatePaymentStatus ( String gid, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			MRD_SERVICE,
			user,
			"Updating payment status for MRD with GID: " + gid);

		// validate user access
		checkUserAccess(user, "update payment status");

		// find the MRD by GID
		var mrd = mrdRepository.findByGid(gid)
			.orElseThrow(() -> {
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.UPDATE,
					MRD_SERVICE,
					user,
					GID_NOT_FOUND + gid,
					null);
				return new GIDNotFoundException(GID_NOT_FOUND + gid);
			});

		// toggle the payment status and save the MRD
		mrd.setHasPaid(! mrd.isHasPaid());
		MRD updatedMRD = mrdRepository.save(mrd);

		// log the successful update
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			MRD_SERVICE,
			user,
			"Updated payment status for GID " + gid + ": " + updatedMRD.isHasPaid());

		// return the response
		return from(updatedMRD.getGid());
	}

	public List< MRDResponse > getUserMRDs ( String email, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			"Fetching MRDs for user with email: " + email + " and role: " + user.getRole());

		// validate user access
		checkUserAccess(user, "fetch MRDs");

		// find the user
		var existingUser = fetchUserByEmail(email);

		// fetch MRDs for the user
		var mrds = mrdRepository.findAllByUser(existingUser);

		// log the successful retrieval
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			FETCHED + mrds.size() + " MRDs for user with email: " + email);

		// return the list of MRDResponse objects
		return mrds.stream()
			.map(MRDService::apply)
			.toList();
	}

	public List< String > getUserGids ( String email, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			"Fetching GIDs for user with email: " + email);

		// validate user access
		checkUserAccess(user, "fetch GIDs");

		// find the user
		var existingUser = userRepository.findUserByEmail(email)
			.orElseThrow(() -> {
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.READ,
					MRD_SERVICE,
					user,
					USER_NOT_FOUND_WITH_EMAIL + email,
					null);
				return new UserNotFoundException(USER_NOT_FOUND_WITH_EMAIL + email);
			});

		// fetch MRDs for the user
		var mrds = mrdRepository.findAllByUser(existingUser);

		// log the successful retrieval
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			FETCHED + mrds.size() + " MRDs for user with email: " + email);

		// map MRDs to GIDs
		List< String > gids = mrds.stream()
			.map(MRD::getGid)
			.toList();

		// log the successful retrieval of GIDs
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			FETCHED + gids.size() + " GIDs for user with email: " + email);

		// return the list of GIDs
		return gids;
	}

	public MRDResponse getMRDbyGID ( String gid, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			"Fetching MRD with GID: " + gid);

		// validate user access
		checkUserAccess(user, "fetch MRD by GID");

		// find the MRD by GID
		var mrd = mrdRepository.findByGid(gid)
			.orElseThrow(() -> {
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.READ,
					MRD_SERVICE,
					user,
					GID_NOT_FOUND + gid,
					null);
				return new GIDNotFoundException(GID_NOT_FOUND + gid);
			});

		// log the successful retrieval
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			"Fetched MRD with GID: " + gid);

		// return the MRDResponse object
		return from(mrd.getGid());
	}

	public List< MRDResponse > getAllMRDs ( User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			"Fetching all MRDs for user with role: " + user.getRole());

		// validate user access
		checkUserAccess(user, "fetch all MRDs");

		// find all MRDs
		var mrds = mrdRepository.findAll();

		// log the successful retrieval
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			MRD_SERVICE,
			user,
			FETCHED + mrds.size() + " MRDs");

		// return the list of MRDResponse objects
		return mrds.stream()
			.map(MRDService::apply)
			.toList();
	}

	/*
	 * private methods - isUsed internally
	 */

	private MRD createandSaveMRD ( String email ) {
		// find the user
		var user = fetchUserByEmail(email);

		// create new MRD registration with a unique GID
		var mrd = MRD.builder()
			.user(user)
			.gid(generateGID())
			.name(user.getName())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.college(user.getCollege())
			.year(user.getYear())
			.department(user.getDepartment())
			.rollNo(user.getRollNo())
			.build();

		// save the MRD registration
		MRD savedMRD = mrdRepository.save(mrd);

		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			MRD_SERVICE,
			null,
			"MRD registration completed for user " + user.getUserId() + ": GID=" + savedMRD.getGid()
		);

		return savedMRD;
	}

	private void validateUserForRegistration ( User user ) {
		if ( ! user.isVerified() ) {
			LoggingUtil.logError(log, MessageConstant.Operation.CREATE, MRD_SERVICE, user, "Email not verified: " + user.getUserId(), null);
			throw new EmailNotVerifiedException("You need to verify your email before registering for Paridhi");
		}

		if ( ! user.isProfileCreated() ) {
			LoggingUtil.logError(log, MessageConstant.Operation.CREATE, MRD_SERVICE, user, "Profile not created: " + user.getUserId(), null);
			throw new ProfileNotYetCreatedException();
		}
	}

	private User createAndSaveUserByAdmin ( AdminMRDRequest request ) {
		var user = User.builder()
			.name(request.getName())
			.email(request.getEmail())
			.password(passwordEncoder.encode(PASSWORD))
			.phoneNumber(request.getContact())
			.college(request.getCollege())
			.year(request.getYear())
			.department(request.getDepartment())
			.rollNo(request.getRollNo())
			.isVerified(true)
			.isProfileCreated(true)
			.build();

		User savedUser = userRepository.save(user);

		LoggingUtil.logOperation(log, MessageConstant.Operation.CREATE, MRD_SERVICE, null, "New user created by Admin: " + request.getEmail());

		return savedUser;
	}

	private User fetchUserByEmail ( String email ) {
		return userRepository.findUserByEmail(email)
			.orElseThrow(() -> {
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.CREATE,
					MRD_SERVICE,
					null,
					USER_NOT_FOUND_WITH_EMAIL + email,
					null);
				return new UserNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.USER));
			});
	}

	private String generateGID () {
		Long sequence = stringRedisTemplate.opsForValue().increment(MRD_SEQ_KEY);
		String paddedSequence = String.format("%04d", sequence);
		String salt = RandomUtil.generateGidSalt(2);

		return "PD26-" + paddedSequence + "-" + salt;
	}

	private void checkUserAccess ( User user, String methodName ) {
		// check if the user is null
		if ( user == null ) {
			LoggingUtil.logSecurity(
				log,
				AppConstant.ACCESS_DENIED,
				null,
				AppConstant.FAILED,
				"User is not authenticated to " + methodName);
			throw new ForbiddenAccessException("You do not have permission to access this resource");
		}

		// check if the user has ROLE_USER
		if ( user.getRole().equals(Role.ROLE_USER) ) {
			LoggingUtil.logSecurity(
				log,
				AppConstant.ACCESS_DENIED,
				user,
				AppConstant.FAILED,
				"User with ID " + user.getUserId() + " not authorized to " + methodName);
			throw new ForbiddenAccessException("User not authorized to " + methodName);
		}

		// log the successful authorization
		LoggingUtil.logSecurity(
			log,
			AppConstant.ACCESS_GRANTED,
			user,
			AppConstant.SUCCESS,
			"User with ID " + user.getUserId() + " authorized to " + methodName);
	}
}