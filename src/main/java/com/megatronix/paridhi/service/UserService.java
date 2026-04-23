package com.megatronix.paridhi.service;

import com.megatronix.paridhi.dto.request.AdminCreationRequest;
import com.megatronix.paridhi.dto.request.LoginRequest;
import com.megatronix.paridhi.dto.request.RegisterRequest;
import com.megatronix.paridhi.dto.response.AuthResponse;
import com.megatronix.paridhi.dto.response.TokenValidationResponse;
import com.megatronix.paridhi.dto.response.UserResponse;
import com.megatronix.paridhi.exception.UserAlreadyExistsException;
import com.megatronix.paridhi.exception.UserNotFoundException;
import com.megatronix.paridhi.model.BlacklistedToken;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.BlacklistedTokenRepository;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.security.JwtService;
import com.megatronix.paridhi.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.megatronix.paridhi.constant.AppConstant.USER;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.ALREADY_EXISTS;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.EMAIL_NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.CREATE;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.READ;
import static com.megatronix.paridhi.constant.Role.ROLE_ADMIN;

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
		log.info("Register request for email: {}", request.email());

		if ( userRepository.existsByEmail(request.email()) ) {
			LoggingUtil.logError(log, READ, USER, null, String.format(ALREADY_EXISTS, USER, "email", request.email()), null);
			throw new UserAlreadyExistsException("User already exists");
		}

		// We don't specify the role here, so it automatically defaults to ROLE_USER!
		User user = User.builder()
			.name(request.name())
			.email(request.email())
			.password(passwordEncoder.encode(request.password()))
			.build();

		User savedUser = userRepository.save(user);
		log.info("Registered user successfully: {}", savedUser.getEmail());

		// User model implements UserDetails, so we pass it directly
		var jwtToken = jwtService.generateToken(savedUser);

		// Using your @Builder on the AuthResponse Record
		return AuthResponse.builder()
			.token(jwtToken)
			.user(UserResponse.fromUser(savedUser))
			.build();
	}

	@Transactional
	public AuthResponse login ( LoginRequest request ) {
		log.info("Login attempt for email: {}", request.email());

		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.email(), request.password())
		);

		User user = userRepository.findUserByEmail(request.email())
			.orElseThrow(() -> {
				LoggingUtil.logError(log, READ, USER, null, String.format(EMAIL_NOT_FOUND, request.email()), null);
				return new UserNotFoundException("User not found");
			});

		user.setLastLogin(Instant.now());

		var jwtToken = jwtService.generateToken(user);
		log.info("User logged in successfully: {}", user.getEmail());

		return AuthResponse.builder()
			.token(jwtToken)
			.user(UserResponse.fromUser(user))
			.build();
	}

	@Transactional
	public AuthResponse createAdmin ( AdminCreationRequest request ) {
		log.info("Admin creation request for email: {}", request.email());

		if ( userRepository.existsByEmail(request.email()) ) {
			LoggingUtil.logError(log, CREATE, USER, null, String.format(ALREADY_EXISTS, "Admin user",
				"email", request.email()), null);
			throw new UserAlreadyExistsException("User already exists");
		}

		// Explicitly override the default to make them an ADMIN
		User user = User.builder()
			.name(request.name())
			.email(request.email())
			.password(passwordEncoder.encode(request.password()))
			.role(ROLE_ADMIN)
			.build();

		User savedUser = userRepository.save(user);
		log.info("Admin created successfully: {}", savedUser.getEmail());

		var jwtToken = jwtService.generateToken(savedUser);

		return AuthResponse.builder()
			.token(jwtToken)
			.user(UserResponse.fromUser(savedUser))
			.build();
	}

	@Transactional
	public void logout ( String token ) {
		String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
		Instant expiresAt = jwtService.getExpirationDateFromToken(jwt);

		var blacklistedToken = BlacklistedToken.builder()
			.token(jwt)
			.expiresAt(expiresAt)
			.build();

		blacklistedTokenRepository.save(blacklistedToken);
		log.info("Token blacklisted successfully");
	}

	@Transactional ( readOnly = true )
	public TokenValidationResponse validateCurrentToken ( String authHeader ) {
		if ( authHeader == null || ! authHeader.startsWith("Bearer ") ) {
			return new TokenValidationResponse(false, "Invalid authorization header", null);
		}

		String token = authHeader.substring(7);

		try {
			if ( jwtService.isTokenBlacklisted(token) ) {
				return new TokenValidationResponse(false, "Token has been revoked", null);
			}

			String username = jwtService.extractUsername(token);
			if ( username == null || jwtService.isTokenExpired(token) ) {
				return new TokenValidationResponse(false, "Token is invalid or expired", null);
			}

			User user = userRepository.findUserByEmail(username)
				.orElseThrow(() -> new UserNotFoundException("User not found"));

			if ( jwtService.isTokenValid(token, user) ) {
				return new TokenValidationResponse(true, "Token is valid", UserResponse.fromUser(user));
			}

			return new TokenValidationResponse(false, "Token is invalid", null);

		} catch ( Exception e ) {
			log.error("Token validation error: {}", e.getMessage());
			return new TokenValidationResponse(false, "Error validating token", null);
		}
	}

	@Transactional
	@Scheduled ( cron = "0 0 0 * * ?" )
	public void cleanUpExpiredTokens () {
		log.info("Cleaning up expired Blacklisted tokens");
		blacklistedTokenRepository.deleteAllExpiredTokens(Instant.now());
	}
}