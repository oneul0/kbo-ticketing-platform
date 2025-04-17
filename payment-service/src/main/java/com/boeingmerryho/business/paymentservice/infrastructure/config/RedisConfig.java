package com.boeingmerryho.business.paymentservice.infrastructure.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password}")
	private String password;

	@Value("${user.redis.host}")
	private String globalHost;

	@Value("${user.redis.port}")
	private int globalPort;

	@Value("${user.redis.password}")
	private String globalPassword;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		config.setUsername("default");
		config.setPassword(password);
		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisConnectionFactory redisConnectionGlobalFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(globalHost, globalPort);
		config.setUsername("default");
		config.setPassword(globalPassword);
		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(
		RedisConnectionFactory redisConnectionGlobalFactory) {

		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionGlobalFactory);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.registerModule(new SimpleModule());

		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);
		template.afterPropertiesSet();
		return template;
	}

	@Bean
	public RedisTemplate<String, PaymentSession> redisTemplateForKakaoPaymentSession(
		RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, PaymentSession> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(PaymentSession.class));
		return template;
	}

	@Bean
	public RedisTemplate<String, LocalDateTime> redisTemplateForPaymentExpiredTime(
		RedisConnectionFactory redisConnectionFactory) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		GenericJackson2JsonRedisSerializer serializer =
			new GenericJackson2JsonRedisSerializer(objectMapper);

		RedisTemplate<String, LocalDateTime> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);
		return template;
	}

	@Bean
	public RedisTemplate<String, Integer> redisTemplateForPaymentPrice(
		RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Integer> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Integer.class));
		return template;
	}

}