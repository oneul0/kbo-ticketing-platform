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
public class TicketRedisConfig {

	@Value("${spring.redis.ticket-server.host}")
	private String host;

	@Value("${spring.redis.ticket-server.port}")
	private int port;

	@Value("${spring.redis.ticket-server.name}")
	private String username;

	@Value("${spring.redis.ticket-server.password}")
	private String password;

	@Bean
	public RedisConnectionFactory ticketRedisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		config.setUsername(username);
		config.setPassword(password);
		return new LettuceConnectionFactory(config);
	}

	@Bean(name = "ticketRedisTemplate")
	public RedisTemplate<String, Object> ticketRedisTemplate(RedisConnectionFactory ticketRedisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(ticketRedisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}
