package com.boeingmerryho.business.userservice.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MailConfig {

	@Value("${spring.mail.host}")
	private String host;

	@Value("${spring.mail.port}")
	private int port;

	@Value("${spring.mail.username}")
	private String username;

	@Value("${spring.data.redis.password}")
	private String password;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String auth;

	@Bean
	// public JavaMailSender javaMailSender() {
	//
	// 	log.info("sender email address : {}", username);
	// 	log.info("sender password : {}", password);
	//
	// 	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	// 	mailSender.setHost(host);
	// 	mailSender.setPort(port);
	// 	mailSender.setUsername(username);
	// 	mailSender.setPassword(password);
	//
	// 	Properties props = mailSender.getJavaMailProperties();
	// 	props.put("mail.transport.protocol", "smtp");
	// 	props.put("mail.smtp.auth", auth);
	// 	props.put("mail.smtp.starttls.enable", true);
	//
	// 	return mailSender;
	// }
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(username);
		mailSender.setPassword(password);

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		javaMailProperties.put("mail.debug", "true");
		javaMailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		javaMailProperties.put("mail.smtp.ssl.protocols", "TLSv1.3");

		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}
}
