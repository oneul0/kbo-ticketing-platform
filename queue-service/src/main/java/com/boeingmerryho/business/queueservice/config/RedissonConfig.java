package com.boeingmerryho.business.queueservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Profile("!test")
@Configuration
public class RedissonConfig {

	private static final String REDISSON_HOST_PREFIX = "redis://";

	@Value("${spring.data.redis.store-queue.host}")
	private String storeQueueHost;

	@Value("${spring.data.redis.store-queue.port}")
	private int storeQueuePort;

	@Value("${spring.data.redis.store-queue.username}")
	private String storeQueueUsername;

	@Value("${spring.data.redis.store-queue.password}")
	private String storeQueuePassword;

	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClientForStoreQueue() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress(REDISSON_HOST_PREFIX + storeQueueHost + ":" + storeQueuePort)
			.setUsername(storeQueueUsername)
			.setPassword(storeQueuePassword);
		return Redisson.create(config);
	}

	@Bean(name = "redisTemplateForStoreQueueRedis")
	public RedisTemplate<String, Object> redisTemplateForStoreQueueRedis(
		RedisConnectionFactory redisConnectionFactoryForStoreQueueRedis) {

		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactoryForStoreQueueRedis);

		ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.registerModule(new Jdk8Module())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();

		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);
		template.afterPropertiesSet();

		return template;
	}
}
