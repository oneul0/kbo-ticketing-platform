package com.boeingmerryho.business.paymentservice.infrastructure.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password}")
	private String password;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		config.setPassword(password);
		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisTemplate<String, PaymentSession> redisTemplateForKakaoPaymentSession(
		RedisConnectionFactory factory) {
		RedisTemplate<String, PaymentSession> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(PaymentSession.class));
		return template;
	}

	@Bean
	public RedisTemplate<String, LocalDateTime> redisTemplateForPaymentExpiredTime(
		RedisConnectionFactory factory) {
		RedisTemplate<String, LocalDateTime> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(LocalDateTime.class));
		return template;
	}

}