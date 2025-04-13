package com.boeingmerryho.business.queueservice.application.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.config.RedisTestContainersConfig;

@ExtendWith(SpringExtension.class)
@Import(RedisTestContainersConfig.class)
@ContextConfiguration(classes = RedisTestContainersConfig.class)
public class QueueJoinServiceConcurrencyTest {

	@Autowired
	private QueueService queueJoinService;  // 테스트할 서비스

	@Autowired
	private RedissonClient redissonClient;  // Redisson 클라이언트

	private static final Long STORE_ID = 1L;
	private static final Long USER_ID = 123L;
	private static final Long TICKET_ID = 456L;

	@BeforeEach
	void setUp() {
		// 테스트 시작 전 필요한 초기 설정
	}

	@AfterEach
	void tearDown() {
		// 테스트 후 정리 작업
	}

	@Test
	@Timeout(10)
	void testJoinQueueConcurrency() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		Set<Long> registeredUserIds = Collections.synchronizedSet(new HashSet<>());

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					RLock lock = redissonClient.getLock("queue:join:" + STORE_ID + ":" + USER_ID);
					lock.lock();
					try {
						QueueJoinServiceDto dto = new QueueJoinServiceDto(STORE_ID, USER_ID, TICKET_ID);
						queueJoinService.joinQueue(dto);

						registeredUserIds.add(USER_ID);
					} finally {
						lock.unlock();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Assertions.assertEquals(1, registeredUserIds.size(), "유저는 중복 등록되지 않아야 합니다.");
	}
}
