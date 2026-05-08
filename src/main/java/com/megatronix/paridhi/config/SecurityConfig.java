package com.megatronix.paridhi.config;

import com.megatronix.paridhi.filter.EndpointVisibilityFilter;
import com.megatronix.paridhi.filter.RateLimitingFilter;
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
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final RateLimitingFilter rateLimitingFilter;
	private final JwtAuthenticationFilter jwtAuthFilter;
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;
	private final EndpointVisibilityFilter endpointVisibilityFilter;

	public SecurityConfig (
		RateLimitingFilter rateLimitingFilter,
		JwtAuthenticationFilter jwtAuthFilter,
		UserDetailsServiceImpl userDetailsService,
		JwtAuthenticationEntryPoint jwtAuthEntryPoint,
		EndpointVisibilityFilter endpointVisibilityFilter ) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.jwtAuthEntryPoint = jwtAuthEntryPoint;
		this.rateLimitingFilter = rateLimitingFilter;
		this.userDetailsService = userDetailsService;
		this.endpointVisibilityFilter = endpointVisibilityFilter;
	}

	private static final String[] WHITELISTED_URLS = {
		"/", // Landing page
		"/actuator/**", // Actuator endpoints
		"/api/auth/**", // Auth, Password Reset, and OTP Controllers
		"/api/events/**", // Public event browsing
		"/api/event-combos/**", // Public combo browsing
		"/api/mrd/register", // Public registration
	};

	private static final String[] SWAGGER_URLS = {
		"/swagger-ui/**", // Swagger UI
		"/v3/api-docs/**", // OpenAPI docs
		"/swagger-ui.html", // Swagger UI HTML
	};

	@Bean
	public SecurityFilterChain securityFilterChain ( HttpSecurity http ) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(WHITELISTED_URLS).permitAll()
				.requestMatchers(SWAGGER_URLS).hasAnyRole("ADMIN", "SUPERADMIN")
				.anyRequest().authenticated())
			.httpBasic(Customizer.withDefaults())
			.exceptionHandling(exception -> {
				// use JWT entry point for API endpoints to return JSON errors
				exception.defaultAuthenticationEntryPointFor(
					jwtAuthEntryPoint,
					PathPatternRequestMatcher.withDefaults().matcher("/api/**"));

				// use Basic Auth entry point for Swagger to trigger browser login popup
				BasicAuthenticationEntryPoint basicEntryPoint = new BasicAuthenticationEntryPoint();
				basicEntryPoint.setRealmName("Paridhi Admin Portal");
				try {
					basicEntryPoint.afterPropertiesSet();
				} catch ( Exception e ) {
					throw new IllegalStateException("Failed to initialize BasicAuthenticationEntryPoint", e);
				}

				PathPatternRequestMatcher.Builder pathBuilder = PathPatternRequestMatcher.withDefaults();

				RequestMatcher swaggerMatcher = new OrRequestMatcher(
					pathBuilder.matcher("/swagger-ui/**"),
					pathBuilder.matcher("/v3/api-docs/**"),
					pathBuilder.matcher("/swagger-ui.html")
				);

				exception.defaultAuthenticationEntryPointFor(basicEntryPoint, swaggerMatcher);
			})
			.sessionManagement(session -> session
				.sessionCreationPolicy(STATELESS))
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class)
			.addFilterBefore(endpointVisibilityFilter, RateLimitingFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider () {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(
			userDetailsService);
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
