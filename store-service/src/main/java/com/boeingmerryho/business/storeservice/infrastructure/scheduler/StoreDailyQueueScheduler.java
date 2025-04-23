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
		// TODO 배치로 id만 가져온다든지, 특정 가게만 가져온다든지 이벤트 payload에 넣고 뿌리기
		// TODO Lock이 안걸려있기 때문에 Lock 추가해야함
		// TODO 인덱싱 처리하면 가게 날짜별로 검색할때에도 빠르게 조회 검색 가능!

		for (Store store : stores) {
			LocalDateTime todayOpenAt = LocalDateTime.of(LocalDate.now(), store.getOpenAt().toLocalTime());
			LocalDateTime todayClosedAt = LocalDateTime.of(LocalDate.now(), store.getClosedAt().toLocalTime());

			storeQueueSchedulerProducer.scheduleEnableMessage(store.getId(), todayOpenAt);
			storeQueueSchedulerProducer.scheduleDisableMessage(store.getId(), todayClosedAt);
		}
	}
}
