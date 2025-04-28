package com.boeingmerryho.infrastructure.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricConfig {

	@Bean
	public TimedAspect timedAspect(MeterRegistry meterRegistry) {
		return new TimedAspect(meterRegistry);
	}

	@Bean
	public CountedAspect countedAspect(MeterRegistry meterRegistry) {
		return new CountedAspect(meterRegistry);
	}
}