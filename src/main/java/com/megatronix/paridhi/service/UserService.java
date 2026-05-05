package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.dto.request.AdminCreationRequest;
import com.megatronix.paridhi.dto.request.LoginRequest;
import com.megatronix.paridhi.dto.request.RegisterRequest;
import com.megatronix.paridhi.dto.response.AuthResponse;
import com.megatronix.paridhi.dto.response.TokenValidationResponse;
import com.megatronix.paridhi.dto.response.UserResponse;
import com.megatronix.paridhi.exception.UserAlreadyExistsException;
import com.megatronix.paridhi.model.BlacklistedToken;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.BlacklistedTokenRepository;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.megatronix.paridhi.constant.AppConstant.Common.FAILED;
import static com.megatronix.paridhi.constant.AppConstant.Common.SUCCESS;
import static com.megatronix.paridhi.constant.AppConstant.Resource.*;
import static com.megatronix.paridhi.constant.AppConstant.Security.BEARER;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.*;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.EXPIRED_BLACKLISTED_TOKENS_PURGED;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static com.megatronix.paridhi.constant.MessageConstant.SuccessTemplate.CREATED;
import static com.megatronix.paridhi.constant.MessageConstant.SuccessTemplate.USER_AUTHENTICATED_SUCCESSFULLY;
import static com.megatronix.paridhi.constant.MessageConstant.ToastMessage.LOGIN_SUCCESSFUL;
import static com.megatronix.paridhi.constant.MessageConstant.ToastMessage.REGISTRATION_SUCCESSFUL;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.JWT_TOKEN_VALIDATION_FAILED;
import static com.megatronix.paridhi.constant.Role.ROLE_ADMIN;
import static com.megatronix.paridhi.dto.response.AuthResponse.from;
import static com.megatronix.paridhi.dto.response.TokenValidationResponse.invalid;
import static com.megatronix.paridhi.dto.response.TokenValidationResponse.valid;
import static com.megatronix.paridhi.util.LoggingUtil.*;
import static java.lang.String.format;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final BlacklistedTokenRepository blacklistedTokenRepository;

	public UserService (
		JwtService jwtService,
		UserRepository userRepository,
		BCryptPasswordEncoder passwordEncoder,
		AuthenticationManager authenticationManager,
		BlacklistedTokenRepository blacklistedTokenRepository
	) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.blacklistedTokenRepository = blacklistedTokenRepository;
	}

	@Transactional
	public AuthResponse register ( RegisterRequest request ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Processing registration request for email: {}", request.email());
		}

		// step-1: check if USER already exists with the EMAIL
		if ( userRepository.existsByEmail(request.email()) ) {
			logSecurity(log, USER_REGISTER, null, FAILED, format("Registration rejected - " + ALREADY_EXISTS, USER, EMAIL, request.email()));
			throw new UserAlreadyExistsException();
		}

		// step-2: create the USER and save it in the DB
		User user =
			User.builder().name(request.name()).email(request.email()).password(passwordEncoder.encode(request.password())).isLoggedIn(true).isVerified(true).build();

		User savedUser = userRepository.save(user);
		if ( log.isDebugEnabled() ) {
			log.debug("User entity saved to database with ID: {}", savedUser.getUserId());
		}
		logSecurity(log, USER_REGISTER, savedUser, SUCCESS, format(CREATED, USER, savedUser.getUserId()));

		// step-3: generate the JWT token for the saved USER
		var jwtToken = jwtService.generateToken(savedUser);

		return from(jwtToken, REGISTRATION_SUCCESSFUL);
	}

	@Transactional
	public AuthResponse login ( LoginRequest request ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Attempting authentication for email: {}", request.email());
		}

		// step-1: authenticate the USER using AuthenticationManager
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		// step-2: fetch the USER from the DB
		User user = fetchUserByEmail(request.email());

		// step-3: update last login time and loggedIn status and generate the JWT token for the USER
		user.setLoggedIn(true);
		user.setLastLogin(Instant.now());

		// step-4: generate the JWT token for the saved USER
		var jwtToken = jwtService.generateToken(user);
		logSecurity(log, USER_LOGIN, user, SUCCESS, format(USER_AUTHENTICATED_SUCCESSFULLY, user.getEmail()));

		return from(jwtToken, LOGIN_SUCCESSFUL);
	}

	@Transactional
	public AuthResponse createAdmin ( AdminCreationRequest request ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Processing admin creation request for email: {}", request.email());
		}

		// step-1: check if ADMIN already exists with the EMAIL
		if ( userRepository.existsByEmail(request.email()) ) {
			logSecurity(log, ADMIN_CREATE, null, FAILED, format(ADMIN + " Creation rejected - " + ALREADY_EXISTS, ADMIN, EMAIL, request.email()));
			throw new UserAlreadyExistsException();
		}

		// step-2: create the USER with role: ADMIN and save it in the DB
		User user = User.builder().name(request.name()).email(request.email()).password(passwordEncoder.encode(request.password())).role(ROLE_ADMIN).build();

		User savedUser = userRepository.save(user);
		logSecurity(log, ADMIN_CREATE, savedUser, SUCCESS, format(CREATED, ADMIN, savedUser.getUserId()));

		// step-3: generate the JWT token for the saved USER
		var jwtToken = jwtService.generateToken(savedUser);

		return from(jwtToken, format(CREATED, ADMIN, savedUser.getUserId()));
	}

	@Transactional
	public void logout ( String authHeader ) {
		// step-1: validate the auth header and extract the token
		String token = authHeader.startsWith(BEARER) ? authHeader.substring(BEARER.length()) : authHeader;

		// step-2: extract the username from the token
		String username = jwtService.extractUsername(token);

		// step-3: fetch the USER from the DB and set loggedIn status to false
		User user = fetchUserByEmail(username);
		user.setLoggedIn(false);

		// step-4: blacklist the token by saving it in the blacklisted tokens repository
		var blacklistedToken = BlacklistedToken.builder().token(token).expiresAt(jwtService.getExpirationDateFromToken(token)).build();

		blacklistedTokenRepository.save(blacklistedToken);
		logSecurity(log, USER_LOGOUT, user, SUCCESS, JWT_TOKEN_BLACKLISTED);
	}

	@Transactional ( readOnly = true )
	public TokenValidationResponse validateCurrentToken ( String authHeader ) {
		// step-1: validate the auth header and extract the token
		if ( authHeader == null || ! authHeader.startsWith(BEARER) ) {
			return invalid(INVALID_AUTH_HEADER);
		}
		String token = authHeader.substring(BEARER.length());

		try {
			// step-2: check if the token is blacklisted
			if ( jwtService.isTokenBlacklisted(token) ) {
				return invalid(JWT_TOKEN_REVOKED);
			}

			// step-3: extract the username from the token and validate the token
			String username = jwtService.extractUsername(token);
			if ( username == null || jwtService.isTokenExpired(token) ) {
				return invalid(JWT_TOKEN_EXPIRED);
			}

			// step-4: fetch the USER from the DB and validate the token against the USER details
			User user = fetchUserByEmail(username);
			if ( jwtService.isTokenValid(token, user) ) {
				return valid(UserResponse.fromUser(user));
			}

			// if token is not valid for any reason, return invalid response
			return invalid(JWT_TOKEN_INVALID);
		} catch ( Exception e ) {
			logError(log, TOKEN_VALIDATION, USER, null, MessageConstant.ErrorTemplate.JWT_TOKEN_VALIDATION_FAILED, e);
			return invalid(JWT_TOKEN_VALIDATION_FAILED);
		}
	}

	@Transactional
	@Scheduled ( cron = "0 0 0 * * ?" )
	public void cleanUpExpiredTokens () {
		if ( log.isInfoEnabled() ) {
			log.info("Initiating scheduled cleanup of expired blacklisted tokens");
		}
		// delete all blacklisted tokens that have expired
		blacklistedTokenRepository.deleteAllExpiredTokens(Instant.now());
		logOperation(log, TOKEN_CLEANUP, AppConstant.Resource.BLACKLISTED_TOKEN, null, EXPIRED_BLACKLISTED_TOKENS_PURGED);
	}

	private User fetchUserByEmail ( String email ) {
		// fetch the USER from the DB using the email, if not found throw an exception
		return userRepository.findUserByEmail(email).orElseThrow(() -> {
			logSecurity(log, USER_LOGIN, null, FAILED, format(NOT_FOUND_LOG, USER, EMAIL, email));
			return new BadCredentialsException(INVALID_CREDENTIALS);
		});
	}
}