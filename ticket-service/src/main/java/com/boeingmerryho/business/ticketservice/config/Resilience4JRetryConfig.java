package com.boeingmerryho.business.ticketservice.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import feign.FeignException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

@Configuration
@EnableRetry
public class Resilience4JRetryConfig {

	@Bean
	public Retry retry() {
		RetryConfig config = RetryConfig.custom()
			.maxAttempts(3) // 최대 재시도 횟수
			.waitDuration(Duration.ofSeconds(1)) // 재시도 간 대기 시간
			.retryExceptions(FeignException.class) // Feign 예외만 재시도 대상
			.build();

		RetryRegistry registry = RetryRegistry.of(config);
		return registry.retry("paymentRetry");
	}
}