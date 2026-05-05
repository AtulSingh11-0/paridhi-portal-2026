package com.megatronix.paridhi.config;

import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import static com.megatronix.paridhi.constant.Role.ROLE_SUPERADMIN;

@Component
public class InitialAdminSetup implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(InitialAdminSetup.class);
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public InitialAdminSetup (
		UserRepository userRepository,
		BCryptPasswordEncoder passwordEncoder
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Value ( "${super-admin.default.email}" )
	private String defaultSuperAdminEmail;

	@Value ( "${super-admin.default.password}" )
	private String defaultSuperAdminPassword;

	@Value ( "${super-admin.default.name}" )
	private String defaultSuperAdminName;

	@Override
	public void run ( ApplicationArguments args ) {
		// check if any super admin exists
		if ( userRepository.findAll().stream().noneMatch(user -> ROLE_SUPERADMIN.equals(user.getRole())) ) {
			log.info("No super admin found. Creating default super admin");

			// create default super admin and save to database
			User superAdmin = User.builder()
				.email(defaultSuperAdminEmail)
				.name(defaultSuperAdminName)
				.password(passwordEncoder.encode(defaultSuperAdminPassword))
				.role(ROLE_SUPERADMIN)
				.isVerified(true)
				.build();

			userRepository.save(superAdmin);

			log.info("Default super admin created with email:{} ", defaultSuperAdminEmail);
			log.info("Default super admin created with password:{} ", defaultSuperAdminPassword);
		} else {
			log.info("Super admin already exists");
		}
	}
}
