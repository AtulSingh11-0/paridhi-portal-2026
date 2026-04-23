package com.megatronix.paridhi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI customOpenAPI () {
		return new OpenAPI()
			.info(new Info()
				.title("Paridhi Portal 2026 API")
				.version("1.0")
				.description("API documentation for Paridhi application")
				.contact(new Contact()
					.name("Atul Singh")
					.email("cocatul11@gmailcom")
				)
			).components(new Components()
				.addSecuritySchemes("Bearer Authentication", createAPIKeyScheme())
			);
	}

	private SecurityScheme createAPIKeyScheme () {
		return new SecurityScheme()
			.type(HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}
}
