package com.boeingmerryho.business.queueservice.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.service.QueueService;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class QueueServiceConcurrencyTest {

	@Autowired
	QueueService service;

	@Autowired
	QueueHelper helper;

	@Autowired
	QueueApplicationMapper queueApplicationMapper;

	@Test
	@DisplayName("동시에 여러 사용자가 같은 가게 대기열에 등록 요청을 할 경우, 번호를 순차적으로 발급한다.")
	public void joinQueue_concurrentRequests_assignsSequentialQueueNumbers() throws InterruptedException {
		// given
		Long storeId = 1L;
		Long ticketId = 100L;
		int numberOfUsers = 10;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
		CountDownLatch latch = new CountDownLatch(numberOfUsers);
		List<QueueJoinResponseDto> results = Collections.synchronizedList(new ArrayList<>());

		when(helper.validateStoreIsActive(storeId)).thenReturn(true);
		when(helper.validateTicket(any(Date.class), eq(ticketId))).thenReturn(anyLong());

		for (int i = 0; i < numberOfUsers; i++) {
			final Long userId = (long)i + 1;

			doAnswer(invocation -> {
				// simulate joining queue
				return null;
			}).when(helper).joinUserInQueue(storeId, userId, ticketId);

			when(helper.getUserQueuePosition(storeId, userId)).thenReturn(i + 1);

			when(queueApplicationMapper.toQueueJoinResponseDto(storeId, userId, i + 1))
				.thenReturn(new QueueJoinResponseDto(storeId, userId, i + 1));
		}

		// when
		for (int i = 0; i < numberOfUsers; i++) {
			final Long userId = (long)i + 1;
			executor.submit(() -> {
				try {
					QueueJoinServiceDto dto = new QueueJoinServiceDto(storeId, userId, ticketId);
					QueueJoinResponseDto response = service.joinQueue(dto);
					results.add(response);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// then
		assertEquals(numberOfUsers, results.size());

		List<Integer> queueNumbers = results.stream()
			.map(QueueJoinResponseDto::sequence)
			.sorted()
			.toList();

		for (int i = 0; i < numberOfUsers; i++) {
			assertEquals(i + 1, queueNumbers.get(i));
		}
	}

}
