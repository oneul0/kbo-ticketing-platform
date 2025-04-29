package com.boeingmerryho.business.userservice.infrastructure.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.domain.event.UserWithdrawEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserWithdrawEventListener {
	private final RedisUtil redisUtil;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleUserWithdrawEvent(UserWithdrawEvent event) {
		redisUtil.clearRedisUserData(event.getUserId());
	}
}
