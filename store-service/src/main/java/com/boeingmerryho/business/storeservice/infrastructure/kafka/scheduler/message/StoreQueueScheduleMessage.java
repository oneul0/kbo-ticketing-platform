package com.boeingmerryho.business.storeservice.infrastructure.kafka.scheduler.message;

public record StoreQueueScheduleMessage(
	Long storeId,
	boolean enable
) {
	public static StoreQueueScheduleMessage enable(Long storeId) {
		return new StoreQueueScheduleMessage(storeId, true);
	}

	public static StoreQueueScheduleMessage disable(Long storeId) {
		return new StoreQueueScheduleMessage(storeId, false);
	}
}
