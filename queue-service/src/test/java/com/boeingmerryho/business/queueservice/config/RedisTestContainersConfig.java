package com.boeingmerryho.business.queueservice.config;

import java.time.Duration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@TestConfiguration
@Testcontainers
public class RedisTestContainersConfig {

	private static final int REDIS_PORT = 6379;
	private static final String REDIS_IMAGE = "redis:7.4.1-alpine3.20";

	@Container
	private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
		.withExposedPorts(REDIS_PORT)
		.waitingFor(Wait.forListeningPort())
		.withStartupTimeout(Duration.ofSeconds(60));

	static {
		REDIS_CONTAINER.start();
	}

	@DynamicPropertySource
	static void overrideRedisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT));
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		LettuceConnectionFactory factory = new LettuceConnectionFactory(
			REDIS_CONTAINER.getHost(),
			REDIS_CONTAINER.getMappedPort(REDIS_PORT)
		);
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.registerModule(new Jdk8Module())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
		StringRedisSerializer stringSerializer = new StringRedisSerializer();

		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		return template;
	}

	@Bean
	public RedissonClient redissonClient() {
		String address = "redis://" + REDIS_CONTAINER.getHost() + ":" + REDIS_CONTAINER.getMappedPort(REDIS_PORT);
		Config config = new Config();
		config.useSingleServer().setAddress(address);
		return Redisson.create(config);
	}
}
