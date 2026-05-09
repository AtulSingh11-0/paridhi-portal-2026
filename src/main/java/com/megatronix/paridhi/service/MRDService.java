package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.dto.request.AdminMRDRequest;
import com.megatronix.paridhi.dto.response.MRDResponse;
import com.megatronix.paridhi.exception.*;
import com.megatronix.paridhi.model.MRD;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.MRDRepository;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.security.JwtService;
import com.megatronix.paridhi.util.AccessControlUtil;
import com.megatronix.paridhi.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.megatronix.paridhi.constant.AppConstant.Resource.USER;
import static com.megatronix.paridhi.constant.AppConstant.Security.BEARER;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.EMAIL_NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.INVALID_AUTH_HEADER;
import static com.megatronix.paridhi.dto.response.MRDResponse.from;
import static com.megatronix.paridhi.util.LoggingUtil.logError;
import static com.megatronix.paridhi.util.LoggingUtil.logOperation;
import static java.lang.String.format;

@Service
public class MRDService {

	private static final String MRD_SEQ_KEY = "paridhi:mrd:sequence:2026";
	private static final Logger log = LoggerFactory.getLogger(MRDService.class);
	private static final String PASSWORD = RandomUtil.generateRandomPassword(12);

	private final JwtService jwtService;
	private final EmailService emailService;
	private final MRDRepository mrdRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final StringRedisTemplate stringRedisTemplate;

	public MRDService (
		JwtService jwtService,
		EmailService emailService,
		MRDRepository mrdRepository,
		UserRepository userRepository,
		BCryptPasswordEncoder passwordEncoder,
		StringRedisTemplate stringRedisTemplate
	) {
		this.jwtService = jwtService;
		this.emailService = emailService;
		this.mrdRepository = mrdRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.stringRedisTemplate = stringRedisTemplate;
	}

	/*
	 * public methods — doesn't require authentication
	 */

	@Transactional
	public MRDResponse performUserMRD ( String authHeader ) {
		// step-1: validate the Authorization header before proceeding
		if ( authHeader == null || ! authHeader.startsWith(BEARER) ) {
			throw new ForbiddenAccessException(INVALID_AUTH_HEADER);
		}

		// step-2: extract the user's email from the JWT token
		String token = authHeader.substring(BEARER.length());
		String email = jwtService.extractUsername(token);

		if ( log.isDebugEnabled() ) {
			log.debug("MRD registration request received for email: {}", email);
		}

		// step-3: fetch the authenticated user and run pre-registration guards
		var user = fetchUserByEmail(email);
		validateUserForRegistration(user);

		// step-4: create and persist the MRD record
		MRD savedMRD = createAndSaveMRD(user);

		// step-5: send confirmation email with the GID
		emailService.sendMRDWelcome(user.getEmail(), user.getName(), savedMRD.getGid(), user);

		// step-6: audit log the successful registration
		logOperation(log, MRD_REGISTER, AppConstant.Service.MRD, user,
			format(MRD_REGISTERED, user.getEmail(), user.getUserId(), savedMRD.getGid()));

		return from(savedMRD.getGid());
	}

	@Transactional
	public MRDResponse performUserMRDByAdmin ( AdminMRDRequest request, User adminUser ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Admin MRD registration request for email: {}", request.getEmail());
		}

		// step-1: check if a user account already exists for this email
		var existingUserOptional = userRepository.findUserByEmail(request.getEmail());

		if ( existingUserOptional.isPresent() ) {
			// step-2a: user already exists — register MRD against their existing account
			User existingUser = existingUserOptional.get();
			MRD savedMRD = createAndSaveMRD(existingUser);
			emailService.sendMRDWelcome(existingUser.getEmail(), existingUser.getName(), savedMRD.getGid(), existingUser);

			logOperation(log, MRD_REGISTER_BY_ADMIN, AppConstant.Service.MRD, adminUser,
				format(MRD_REGISTERED_BY_ADMIN, existingUser.getEmail(), savedMRD.getGid()));

			return from(savedMRD.getGid());
		}

		// step-2b: user doesn't exist — create a new account and then register MRD
		User newUser = createAndSaveUserByAdmin(request, adminUser);
		MRD savedMRD = createAndSaveMRD(newUser);
		emailService.sendMRDWelcome(newUser.getEmail(), newUser.getName(), savedMRD.getGid(), newUser);

		logOperation(log, MRD_REGISTER_BY_ADMIN, AppConstant.Service.MRD, adminUser,
			format(MRD_REGISTERED_BY_ADMIN, newUser.getEmail(), savedMRD.getGid()));

		return from(savedMRD.getGid());
	}

	/*
	 * protected methods — requires authentication
	 */

	@Transactional
	public MRDResponse updatePaymentStatus ( String gid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update payment status for GID: {}", gid);
		}

		// step-1: security — only ADMIN/SUPERADMIN may update payment status
		AccessControlUtil.requireAdminAccess(log, user, MRD_PAYMENT_UPDATE, AppConstant.Resource.MRD);

		// step-2: fetch the MRD record by GID — throws GIDNotFoundException if missing
		var mrd = mrdRepository.findByGid(gid)
			.orElseThrow(() -> {
				logError(log, MRD_PAYMENT_UPDATE, AppConstant.Service.MRD, user, format(MRD_GID_NOT_FOUND, gid), null);
				return new GIDNotFoundException(format(MRD_GID_NOT_FOUND, gid));
			});

		// step-3: toggle the payment status and persist
		mrd.setHasPaid(! mrd.isHasPaid());
		MRD updatedMRD = mrdRepository.save(mrd);

		// step-4: audit log the successful payment status update
		logOperation(log, MRD_PAYMENT_UPDATE, AppConstant.Service.MRD, user,
			format(MRD_PAYMENT_UPDATED, gid, updatedMRD.isHasPaid(), user.getEmail(), user.getUserId()));

		return from(updatedMRD.getGid());
	}

	@Transactional ( readOnly = true )
	public List< MRDResponse > getUserMRDs ( String email, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching MRDs for email: {} by user: {} (role: {})", email, user.getEmail(), user.getRole());
		}

		// step-1: security — only ADMIN/SUPERADMIN may query MRD records
		AccessControlUtil.requireAdminAccess(log, user, READ, AppConstant.Resource.MRD);

		// step-2: fetch the target user account
		var existingUser = fetchUserByEmail(email);

		// step-3: fetch all MRD records linked to the target user
		var mrds = mrdRepository.findAllByUser(existingUser);

		// step-4: audit log the retrieval
		logOperation(log, READ, AppConstant.Service.MRD, user,
			format(MRDS_FETCHED, mrds.size(), email, user.getEmail(), user.getUserId()));

		return mrds.stream()
			.map(mrd -> from(mrd.getGid()))
			.toList();
	}

	@Transactional ( readOnly = true )
	public List< String > getUserGids ( String email, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching GIDs for email: {} by user: {}", email, user.getEmail());
		}

		// step-1: security — only ADMIN/SUPERADMIN may retrieve GID lists
		AccessControlUtil.requireAdminAccess(log, user, READ, AppConstant.Resource.MRD);

		// step-2: fetch the target user account — throws UserNotFoundException if missing
		var existingUser = userRepository.findUserByEmail(email)
			.orElseThrow(() -> {
				logError(log, READ, AppConstant.Service.MRD, user, format(EMAIL_NOT_FOUND, email), null);
				return new UserNotFoundException(format(NOT_FOUND, USER));
			});

		// step-3: map MRD records to GID strings
		var gids = mrdRepository.findAllByUser(existingUser).stream()
			.map(com.megatronix.paridhi.model.MRD::getGid)
			.toList();

		// step-4: audit log the retrieval
		logOperation(log, READ, AppConstant.Service.MRD, user,
			format(GIDS_FETCHED, gids.size(), email, user.getEmail(), user.getUserId()));

		return gids;
	}

	@Transactional ( readOnly = true )
	public MRDResponse getMRDbyGID ( String gid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching MRD with GID: {} by user: {}", gid, user.getEmail());
		}

		// step-1: security — only ADMIN/SUPERADMIN may look up MRDs by GID
		AccessControlUtil.requireAdminAccess(log, user, READ, AppConstant.Resource.MRD);

		// step-2: fetch the MRD record by GID — throws GIDNotFoundException if missing
		var mrd = mrdRepository.findByGid(gid)
			.orElseThrow(() -> {
				logError(log, READ, AppConstant.Service.MRD, user, format(MRD_GID_NOT_FOUND, gid), null);
				return new GIDNotFoundException(format(MRD_GID_NOT_FOUND, gid));
			});

		// step-3: audit log the successful retrieval
		logOperation(log, READ, AppConstant.Service.MRD, user,
			format(MRD_FETCHED, gid, user.getEmail(), user.getUserId()));

		return from(mrd.getGid());
	}

	@Transactional ( readOnly = true )
	public List< MRDResponse > getAllMRDs ( User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching all MRDs by user: {} (role: {})", user.getEmail(), user.getRole());
		}

		// step-1: security — only ADMIN/SUPERADMIN may retrieve the full MRD list
		AccessControlUtil.requireAdminAccess(log, user, READ, AppConstant.Resource.MRD);

		// step-2: fetch all MRD records
		var mrds = mrdRepository.findAll();

		// step-3: audit log the retrieval
		logOperation(log, READ, AppConstant.Service.MRD, user,
			format(MRDS_ALL_FETCHED, mrds.size(), user.getEmail(), user.getUserId()));

		return mrds.stream()
			.map(mrd -> from(mrd.getGid()))
			.toList();
	}

	/*
	 * private methods — used internally only
	 */

	private MRD createAndSaveMRD ( User user ) {
		// step-1: build the MRD entity with a freshly generated GID and snapshot user details
		var mrd = com.megatronix.paridhi.model.MRD.builder()
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

		// step-2: persist the MRD record
		MRD savedMRD = mrdRepository.save(mrd);

		if ( log.isDebugEnabled() ) {
			log.debug("MRD record persisted — user: {} (ID: {}), GID: {}", user.getEmail(), user.getUserId(), savedMRD.getGid());
		}

		return savedMRD;
	}

	private void validateUserForRegistration ( User user ) {
		// step-1: guard — user must have a verified email before registering for Paridhi
		if ( ! user.isVerified() ) {
			logError(log, MRD_REGISTER, AppConstant.Service.MRD, user,
				format(MRD_EMAIL_NOT_VERIFIED, user.getEmail(), user.getUserId()), null);
			throw new EmailNotVerifiedException("You need to verify your email before registering for Paridhi");
		}

		// step-2: guard — user must have a completed profile before registering
		if ( ! user.isProfileCreated() ) {
			logError(log, MRD_REGISTER, AppConstant.Service.MRD, user,
				format(MRD_PROFILE_NOT_CREATED, user.getEmail(), user.getUserId()), null);
			throw new ProfileNotYetCreatedException();
		}
	}

	private User createAndSaveUserByAdmin ( AdminMRDRequest request, User adminUser ) {
		// step-1: build and persist a new user account with a random temporary password
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

		// step-2: audit log the new account creation
		logOperation(log, MRD_REGISTER_BY_ADMIN, AppConstant.Service.MRD, adminUser,
			format(MRD_USER_CREATED_BY_ADMIN, request.getEmail()));

		return savedUser;
	}

	private User fetchUserByEmail ( String email ) {
		return userRepository.findUserByEmail(email)
			.orElseThrow(() -> {
				logError(log, READ, AppConstant.Service.MRD, null, format(EMAIL_NOT_FOUND, email), null);
				return new UserNotFoundException(format(NOT_FOUND, USER));
			});
	}

	private String generateGID () {
		// atomically increment the Redis-backed sequence and format with zero-padding and a random salt
		Long sequence = stringRedisTemplate.opsForValue().increment(MRD_SEQ_KEY);
		String paddedSequence = format("%04d", sequence);
		String salt = RandomUtil.generateIdSalt(2);
		return "PD26-" + paddedSequence + "-" + salt;
	}

}