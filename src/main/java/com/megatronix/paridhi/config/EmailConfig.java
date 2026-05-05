package com.megatronix.paridhi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
	@Value ( "${spring.mail.username}" )
	private String username;

	@Value ( "${spring.mail.password}" )
	private String password;

	@Value ( "${spring.mail.host}" )
	private String host;

	@Value ( "${spring.mail.port}" )
	private int port;

	@Value ( "${spring.mail.properties.mail.smtp.auth}" )
	private boolean smtpAuth;

	@Value ( "${spring.mail.properties.mail.smtp.starttls.enable}" )
	private boolean starttlsEnable;

	@Value ( "${spring.mail.properties.mail.smtp.ssl.enable}" )
	private boolean sslEnabled;

	@Bean
	public JavaMailSender getJavaMailSender () {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(username);
		mailSender.setPassword(password);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", String.valueOf(smtpAuth));
		props.put("mail.smtp.starttls.enable", String.valueOf(starttlsEnable));
		props.put("mail.smtp.ssl.enable", String.valueOf(sslEnabled));
		props.put("mail.debug", "false");

		return mailSender;
	}
}
