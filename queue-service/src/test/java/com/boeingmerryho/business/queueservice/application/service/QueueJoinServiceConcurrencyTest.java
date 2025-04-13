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
		// 테스트에 제한 시간 설정
	void testJoinQueueConcurrency() throws InterruptedException {
		int threadCount = 100;  // 동시 요청을 보낼 스레드 수
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount); // 스레드 풀
		CountDownLatch latch = new CountDownLatch(threadCount); // 모든 스레드가 끝날 때까지 대기

		// 유저가 중복으로 등록되지 않도록 Set을 사용하여 유일성 체크
		Set<Long> registeredUserIds = Collections.synchronizedSet(new HashSet<>());

		// 여러 스레드에서 joinQueue 메서드를 호출하는 시나리오
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					// 분산 락 설정 (storeId + userId를 키로 사용하여 중복을 방지)
					RLock lock = redissonClient.getLock("queue:join:" + STORE_ID + ":" + USER_ID);
					lock.lock();  // 락을 획득
					try {
						// joinQueue 메서드 호출
						QueueJoinServiceDto dto = new QueueJoinServiceDto(STORE_ID, USER_ID, TICKET_ID);
						queueJoinService.joinQueue(dto);

						// 등록된 userId 추적
						registeredUserIds.add(USER_ID);  // 유저 등록 상태를 추적
					} finally {
						lock.unlock();  // 락 해제
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					latch.countDown(); // 작업 완료 후 countDown
				}
			});
		}

		// 모든 스레드가 작업을 마칠 때까지 기다림
		latch.await();

		// 중복 등록이 없으면 registeredUserIds의 크기는 1이어야 한다
		Assertions.assertEquals(1, registeredUserIds.size(), "유저는 중복 등록되지 않아야 합니다.");
	}
}
