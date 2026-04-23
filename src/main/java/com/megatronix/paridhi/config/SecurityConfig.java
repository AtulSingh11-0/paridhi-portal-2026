package com.megatronix.paridhi.config;

import com.megatronix.paridhi.security.JwtAuthenticationEntryPoint;
import com.megatronix.paridhi.security.JwtAuthenticationFilter;
import com.megatronix.paridhi.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthFilter;
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;

	public SecurityConfig (
		JwtAuthenticationFilter jwtAuthFilter,
		UserDetailsServiceImpl userDetailsService,
		JwtAuthenticationEntryPoint jwtAuthEntryPoint
	) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
		this.jwtAuthEntryPoint = jwtAuthEntryPoint;
	}

	private static final String[] WHITELISTED_URLS = {
		"/",                         // Landing page
		"/api/auth/**",              // Auth, Password Reset, and OTP Controllers
		"/api/phoneNumber",          // Contact submissions
		"/api/events/**",            // Public event browsing
		"/api/combos/**",            // Public combo browsing
		"/api/galleries/**",         // Gallery viewing
		"/api/megatronix-team",      // Public team info
		"/api/domain-posters/**",    // Public posters
		"/api/team-photo",           // Team photos
		"/api/mrd/register",         // Public registration
		"/api/teams/register",       // Public team registration
		"/swagger-ui/**",            // Swagger UI
		"/v3/api-docs/**",           // OpenAPI docs
		"/swagger-ui.html",          // Swagger UI HTML
	};

	/*
	private static final String[] WHITELISTED_URLS = {
		"/", // Landing page
		"/api/auth/**", // Authentication endpoints
		"/api/phoneNumber", // Contact query submission endpoint (POST)
		"/api/events", // Event endpoints
		"/api/events/{id}", // Specific event endpoint
		"/api/events/status", // Event status endpoints
		"/api/events/type/**", // Event type endpoints
		"/api/events/domains/**", // Event domain endpoints
		"/api/combos", // Combo endpoints
		"/api/combos/{id}", // Specific combo endpoint
		"/api/combos/status", // Combo status endpoints
		"/api/combos/domains/**", // Combo domain endpoints
		"/api/galleries", // Gallery endpoints
		"/api/galleries/featured", // Featured gallery endpoints
		"/api/galleries/{id}", // Specific gallery endpoint
		"/api/megatronix-team", // Megatronix team-member endpoints
		"/api/domain-posters", // Domain poster endpoints
		"/api/domain-posters/{domainName}", // Specific domain poster endpoint
		"/api/team-photo", // Team photo endpoints
		"/api/mrd/", // Admin MRD registration endpoint
		"/api/mrd/register", // MRD registration endpoint
		"/api/combos/register",
		"/api/teams/register", // Team registration endpoints
	};
*/

	@Bean
	public SecurityFilterChain securityFilterChain ( HttpSecurity http ) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(WHITELISTED_URLS).permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(jwtAuthEntryPoint)
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(STATELESS)
			)
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider () {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(
			userDetailsService
		);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager ( AuthenticationConfiguration config ) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder () {
		return new BCryptPasswordEncoder();
	}
}
