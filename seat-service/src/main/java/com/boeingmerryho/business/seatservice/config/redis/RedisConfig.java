package com.boeingmerryho.business.seatservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {
	@Value("${spring.data.redis.common.host}")
	private String host;

	@Value("${spring.data.redis.common.username}")
	private String username;

	@Value("${spring.data.redis.common.password}")
	private String password;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, 6379);
		config.setUsername(username);
		config.setPassword(password); // 비밀번호 설정
		return new LettuceConnectionFactory(config);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.registerModule(new Jdk8Module())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(
		RedisConnectionFactory connectionFactory,
		ObjectMapper objectMapper
	) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		GenericJackson2JsonRedisSerializer serializer =
			new GenericJackson2JsonRedisSerializer(objectMapper);

		RedisSerializer<String> stringSerializer = new StringRedisSerializer();

		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);

		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		return template;
	}
}