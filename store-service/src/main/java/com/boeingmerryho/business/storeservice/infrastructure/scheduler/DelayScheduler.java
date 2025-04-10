package com.boeingmerryho.business.storeservice.infrastructure.scheduler;

import java.time.LocalDateTime;

import com.boeingmerryho.business.storeservice.infrastructure.kafka.scheduler.message.StoreQueueScheduleMessage;

public interface DelayScheduler {
	void schedule(LocalDateTime executeAt, StoreQueueScheduleMessage message);
}