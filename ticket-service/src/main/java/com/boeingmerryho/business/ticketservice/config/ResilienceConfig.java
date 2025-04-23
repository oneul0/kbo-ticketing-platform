package com.boeingmerryho.business.ticketservice.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

@Configuration
public class ResilienceConfig {

	@Bean
	public Retry paymentRetry() {
		return Retry.of("paymentRetry", RetryConfig.custom()
			.maxAttempts(3)
			.waitDuration(Duration.ofMillis(1000))
			.build());
	}
}
