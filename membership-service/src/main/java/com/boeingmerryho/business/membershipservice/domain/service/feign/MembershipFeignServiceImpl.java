package com.boeingmerryho.business.membershipservice.domain.service.feign;

import java.time.Year;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.membershipservice.application.dto.request.LoginSuccessRequest;
import com.boeingmerryho.business.membershipservice.application.service.feign.MembershipFeignService;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipFeignServiceImpl implements MembershipFeignService {

	private final MembershipRepository membershipRepository;
	private final RedisTemplate<String, String> redisTemplate;

	private static final String MEMBERSHIP_INFO_PREFIX = "user:membership:info:";

	@Override
	public void handleLoginSuccess(LoginSuccessRequest request) {
		int currentYear = Year.now().getValue();

		membershipRepository
			.findActiveMembershipByUserIdAndSeasonAndIsDeletedFalse(request.userId(), currentYear)
			.ifPresent(membership -> {
				String key = MEMBERSHIP_INFO_PREFIX + request.userId();
				Map<String, Object> membershipInfo = Map.of(
					"name", membership.getName().name()
				);
				redisTemplate.opsForHash().putAll(key, membershipInfo);
			});
	}

	@Override
	public Double getDiscountById(Long userId) {
		int currentYear = Year.now().getValue();

		return membershipRepository.findActiveMembershipByUserIdAndSeasonAndIsDeletedFalse(userId, currentYear)
			.map(Membership::getDiscount)
			.orElse(0.0);
	}
}
