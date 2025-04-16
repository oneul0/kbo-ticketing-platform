package com.boeingmerryho.business.membershipservice.infrastructure.runner;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipAvailableQuantityPreloader implements ApplicationRunner {

	private final MembershipRepository membershipRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	private static final String STOCK_KEY_PREFIX = "membership:stock:";

	@Override
	public void run(ApplicationArguments args) {
		List<Membership> memberships = membershipRepository.findAll();
		for (Membership membership : memberships) {
			String key = STOCK_KEY_PREFIX + membership.getId();
			Integer quantity = membership.getAvailableQuantity();
			redisTemplate.opsForValue().set(key, quantity);
		}
	}
}