package com.boeingmerryho.business.ticketservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.boeingmerryho.business.ticketservice.infrastructure.auditing.CustomAuditorAware;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

	@Bean
	public AuditorAware<Long> auditorProvider() {
		return new CustomAuditorAware();
	}
}
