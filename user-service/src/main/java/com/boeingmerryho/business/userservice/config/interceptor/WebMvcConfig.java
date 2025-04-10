package com.boeingmerryho.business.userservice.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final RedisTemplate<String, Object> redisTemplate;

	public WebMvcConfig(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// @Override
	// public void addInterceptors(InterceptorRegistry registry) {
	// 	// registry.addInterceptor(new AdminCheckInterceptor(redisTemplate))
	// 	// 	.excludePathPatterns(
	// 	// 			"/api/**",
	// 	// 			"/error/**"
	// 	// 	);
	// 	//
	// 	// registry.addInterceptor(new UserCheckInterceptor(redisTemplate))
	// 	// 	.excludePathPatterns(
	// 	// 			"/admin/**",
	// 	// 			"/error/**"
	// 	// 	);
	// }

}