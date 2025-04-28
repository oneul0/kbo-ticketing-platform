package com.boeingmerryho.business.seatservice.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
	@Value("${spring.data.redis.seat.username}")
	private String username;

	@Value("${spring.data.redis.seat.password}")
	private String password;

	@Value("${redisson.config.singleServerConfig.address}")
	private String address;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();

		config.setThreads(16).setNettyThreads(32);

		config.setCodec(new JsonJacksonCodec());
		config.useSingleServer()
			.setAddress(address)
			.setUsername(username)
			.setPassword(password);

		return Redisson.create(config);
	}
}