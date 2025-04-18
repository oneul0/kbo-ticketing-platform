package com.boeingmerryho.business.userservice.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.boeingmerryho.business.userservice.application.utils.jwt.JwtTokenProvider;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtTokenProvider jwtTokenProvider() {
		return new JwtTokenProvider();
	}

}
