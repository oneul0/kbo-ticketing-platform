package com.boeingmerryho.business.userservice.infrastructure.event;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.UserVerificationHelper;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.domain.event.UserLoginFailureEvent;
import com.boeingmerryho.business.userservice.domain.event.UserLoginSuccessEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginEventListener {

	private final RedisUtil redisUtil;
	private final UserVerificationHelper userVerificationHelper;
	private final UserHelper userHelper;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleLoginSuccess(UserLoginSuccessEvent event) {
		redisUtil.updateUserInfo(event.getUser());
		redisUtil.updateUserJwtToken(event.getUserId());

		CompletableFuture.runAsync(() -> {
			try {
				userVerificationHelper.getNotifyLoginResponse(event.getUserId());
			} catch (Exception e) {
				log.error("[Login Notify to MembershipService] failed - userId: {}, error: {}", event.getUserId(),
					e.getMessage(), e);
			}
		});
	}

	@Async
	@EventListener
	public void handleLoginFailure(UserLoginFailureEvent event) {
		userHelper.countLoginFailure(event.getUserId());
		redisUtil.rollbackUserInfo(event.getUserId());
		redisUtil.rollbackUserJwtToken(event.getUserId());
	}
}
