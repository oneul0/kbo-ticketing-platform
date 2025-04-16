package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.entity.MembershipUser;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipUserRepository;
import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipListenerHelper {

	private final RedisTemplate<String, String> redisTemplate;
	private final MembershipRepository membershipJpaRepository;
	private final MembershipUserRepository membershipUserRepository;

	private static final String MEMBERSHIP_USER_PREFIX = "membership:user:";

	@Transactional
	public void handleMembershipSuccess(Long userId) {
		String userKey = MEMBERSHIP_USER_PREFIX + userId;

		String membershipIdValue = redisTemplate.opsForValue().get(userKey);
		if (membershipIdValue == null) {
			throw new GlobalException(MembershipErrorCode.NOT_FOUND_USER);
		}

		Long membershipId = Long.valueOf(membershipIdValue);

		Membership membership = membershipJpaRepository.findByIdAndIsDeletedFalse(membershipId)
			.orElseThrow(() -> new GlobalException(MembershipErrorCode.NOT_FOUND));

		MembershipUser newUser = MembershipUser.builder()
			.userId(userId)
			.season(membership.getSeason())
			.membership(membership)
			.isActive(true)
			.build();

		membershipUserRepository.save(newUser);

		redisTemplate.delete(userKey);
	}
}
