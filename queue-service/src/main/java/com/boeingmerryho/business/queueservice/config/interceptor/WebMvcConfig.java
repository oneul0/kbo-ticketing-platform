package com.boeingmerryho.business.queueservice.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.boeingmerryho.business.queueservice.presentation.interceptor.AdminCheckInterceptor;
import com.boeingmerryho.business.queueservice.presentation.interceptor.UserCheckInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final RedisTemplate<String, Object> redisTemplate;

	public WebMvcConfig(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AdminCheckInterceptor(redisTemplate))
			.excludePathPatterns(
				"/api/**",
				"/error/**"
			);

		registry.addInterceptor(new UserCheckInterceptor(redisTemplate))
			.excludePathPatterns(
				"/admin/**",
				"/error/**"
			);
	}

}