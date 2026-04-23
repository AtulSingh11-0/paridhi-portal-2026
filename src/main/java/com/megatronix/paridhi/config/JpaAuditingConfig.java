package com.megatronix.paridhi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing ( auditorAwareRef = "auditorProvider" )
public class JpaAuditingConfig {

	@Bean
	public AuditorAware< String > auditorProvider () {
		return () -> {
			// get the current authentication from the security context
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			/*
			 * if the authentication is null, not authenticated, or is an anonymous user, return "system" as the auditor name
			 * otherwise, return the name of the authenticated user as the auditor name
			 */
			return checkAuthentication(authentication) ? Optional.of("system") : Optional.of(authentication.getName());
		};
	}

	private boolean checkAuthentication ( Authentication authentication ) {
		// check if the authentication is null, not authenticated, or is an anonymous user
		return authentication == null || ! authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser");
	}
}