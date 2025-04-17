package com.boeingmerryho.business.ticketservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CommonRedisConfig {

	@Value("${spring.redis.common-server.host}")
	private String host;

	@Value("${spring.redis.common-server.port}")
	private int port;

	@Value("${spring.redis.common-server.name}")
	private String username;

	@Value("${spring.redis.common-server.password}")
	private String password;

	@Bean
	public RedisConnectionFactory commonRedisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		config.setUsername(username);
		config.setPassword(password);
		return new LettuceConnectionFactory(config);
	}

	@Bean(name = "commonRedisTemplate")
	public RedisTemplate<String, Object> commonRedisTemplate(RedisConnectionFactory commonRedisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(commonRedisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}
