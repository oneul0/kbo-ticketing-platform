package com.boeingmerryho.business.storeservice.infrastructure.kafka.scheduler;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.storeservice.infrastructure.kafka.scheduler.message.StoreQueueScheduleMessage;
import com.boeingmerryho.business.storeservice.infrastructure.scheduler.DelayScheduler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreQueueSchedulerProducer {

	private final DelayScheduler delayScheduler;

	public void scheduleEnableMessage(Long storeId, LocalDateTime openAt) {
		delayScheduler.schedule(openAt, StoreQueueScheduleMessage.enable(storeId));
	}

	public void scheduleDisableMessage(Long storeId, LocalDateTime closedAt) {
		delayScheduler.schedule(closedAt, StoreQueueScheduleMessage.disable(storeId));
	}
}