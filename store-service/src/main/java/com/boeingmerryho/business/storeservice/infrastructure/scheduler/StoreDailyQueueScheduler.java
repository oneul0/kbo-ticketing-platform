package com.boeingmerryho.business.storeservice.infrastructure.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;
import com.boeingmerryho.business.storeservice.infrastructure.kafka.scheduler.StoreQueueSchedulerProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreDailyQueueScheduler {

	private final StoreRepository storeRepository;
	private final StoreQueueSchedulerProducer storeQueueSchedulerProducer;

	@Scheduled(cron = "0 0 0 * * *")
	public void scheduleDailyQueue() {
		List<Store> stores = storeRepository.findAllByIsDeletedFalse();

		for (Store store : stores) {
			LocalDateTime todayOpenAt = LocalDateTime.of(LocalDate.now(), store.getOpenAt().toLocalTime());
			LocalDateTime todayClosedAt = LocalDateTime.of(LocalDate.now(), store.getClosedAt().toLocalTime());

			storeQueueSchedulerProducer.scheduleEnableMessage(store.getId(), todayOpenAt);
			storeQueueSchedulerProducer.scheduleDisableMessage(store.getId(), todayClosedAt);
		}
	}
}
