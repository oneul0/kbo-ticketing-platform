package com.boeingmerryho.business.storeservice.presentation.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;
import com.boeingmerryho.business.storeservice.exception.StoreErrorCode;
import com.boeingmerryho.business.storeservice.infrastructure.kafka.scheduler.StoreQueueSchedulerProducer;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

//TODO: 배포시 삭제 예정
@RestController
@RequestMapping("/test/schedule")
@RequiredArgsConstructor
public class StoreQueueScheduleTestController {

	private final StoreRepository storeRepository;
	private final StoreQueueSchedulerProducer schedulerProducer;

	@PostMapping("/{storeId}")
	public ResponseEntity<String> testSchedule(@PathVariable Long storeId) {
		Store store = storeRepository.findByIdAndIsDeletedFalse(storeId)
			.orElseThrow(() -> new GlobalException(StoreErrorCode.NOT_FOUND));

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime testOpenAt = now.plusMinutes(1);  // 1분 뒤 오픈
		LocalDateTime testCloseAt = now.plusMinutes(3);  // 2분 뒤 마감

		schedulerProducer.scheduleEnableMessage(store.getId(), testOpenAt);
		schedulerProducer.scheduleDisableMessage(store.getId(), testCloseAt);

		return ResponseEntity.ok("✅ 테스트 메시지 예약 완료: 30초 뒤 오픈 / 2분 뒤 마감");
	}
}
