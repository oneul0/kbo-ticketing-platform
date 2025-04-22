package com.boeingmerryho.business.queueservice.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.service.QueueService;
import com.boeingmerryho.business.queueservice.domain.repository.CustomQueueRepository;
import com.boeingmerryho.business.queueservice.domain.repository.QueueRepository;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class QueueServiceConcurrencyTest {

	@Container
	private static final GenericContainer<?> redis = new GenericContainer<>("redis:6.2")
		.withExposedPorts(6379)
		.withCommand("redis-server --port 6379 --requirepass boeingmerryho")
		.withStartupTimeout(Duration.ofSeconds(60))
		.withReuse(false)
		.withNetworkAliases("redis-test");

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.store-queue.host", redis::getHost);
		registry.add("spring.data.redis.store-queue.port", () -> redis.getMappedPort(6379));
		registry.add("spring.data.redis.store-queue.username", () -> "default");
		registry.add("spring.data.redis.store-queue.password", () -> "boeingmerryho");
		registry.add("spring.redisson.address", () -> "redis://" + redis.getHost() + ":" + redis.getMappedPort(6379));
		registry.add("spring.redisson.password", () -> "boeingmerryho");
	}

	@Autowired
	private QueueService service;

	@MockitoBean
	private QueueHelper helper;

	@Autowired
	private RedisTemplate<String, Object> redisTemplateForStoreQueueRedis;

	@MockitoBean
	private QueueApplicationMapper queueApplicationMapper;

	@MockitoBean
	private QueueRepository queueRepository;

	@MockitoBean
	private CustomQueueRepository customQueueRepository;

	@BeforeEach
	public void setUp() {
		// delete
		redisTemplateForStoreQueueRedis.delete("queue:availability:1");
		redisTemplateForStoreQueueRedis.delete("ticket:info:2025-04-22");
		for (int i = 1; i <= 10; i++) {
			redisTemplateForStoreQueueRedis.delete("ticket:user:" + i);
		}
		redisTemplateForStoreQueueRedis.delete("waitlist:1");
		redisTemplateForStoreQueueRedis.delete("waitlist:1:seq");

		//setup
		for (int i = 1; i <= 10; i++) {
			redisTemplateForStoreQueueRedis.opsForSet().add("ticket:info:2025-04-22", String.valueOf(i));
			redisTemplateForStoreQueueRedis.opsForValue().set("ticket:user:" + i, String.valueOf(i));
		}
		redisTemplateForStoreQueueRedis.opsForValue().set("queue:availability:1", true);
		redisTemplateForStoreQueueRedis.opsForValue().set("waitlist:1:seq", 0L);

		for (int i = 1; i <= 10; i++) {
			log.info("ticket:info:2025-04-22 contains {}: {}", i,
				redisTemplateForStoreQueueRedis.opsForSet().isMember("ticket:info:2025-04-22", String.valueOf(i)));
			log.info("ticket:user:{} value: {}", i,
				redisTemplateForStoreQueueRedis.opsForValue().get("ticket:user:" + i));
		}

		reset(helper);
		when(helper.validateStoreIsActive(1L)).thenReturn(true);
		for (int i = 1; i <= 10; i++) {
			when(helper.validateTicket(any(), eq((long)i))).thenReturn((long)i);
		}
		doNothing().when(helper).joinUserInQueue(eq(1L), anyLong(), anyLong());
		for (int i = 0; i < 10; i++) {
			final Long userId = (long)i + 1;
			when(helper.getUserQueuePosition(1L, userId)).thenReturn(i + 1);
		}

		reset(queueApplicationMapper);
		for (int i = 0; i < 10; i++) {
			final Long userId = (long)i + 1;
			when(queueApplicationMapper.toQueueJoinResponseDto(1L, userId, i + 1))
				.thenReturn(new QueueJoinResponseDto(1L, userId, i + 1));
		}
	}

	@Test
	@DisplayName("동시에 여러 사용자가 같은 가게 대기열에 등록 요청을 할 경우, 번호를 순차적으로 발급한다.")
	public void joinQueue_concurrentRequests_assignsSequentialQueueNumbers() throws InterruptedException {
		// given
		Long storeId = 1L;
		int numberOfUsers = 10;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
		CountDownLatch latch = new CountDownLatch(numberOfUsers);
		List<QueueJoinResponseDto> results = Collections.synchronizedList(new ArrayList<>());

		// when
		for (int i = 0; i < numberOfUsers; i++) {
			final Long userId = (long)i + 1;
			final Long ticketId = userId;
			executor.submit(() -> {
				try {
					QueueJoinServiceDto dto = new QueueJoinServiceDto(storeId, ticketId, userId);
					QueueJoinResponseDto response = service.joinQueue(dto);
					results.add(response);
					log.info("Added result for userId: {} with ticketId: {} and position: {}",
						userId, ticketId, response.sequence());
				} catch (Exception e) {
					log.error("Error during queue join for userId: {}", userId, e);
				} finally {
					latch.countDown();
				}
			});
		}

		boolean completed = latch.await(5, TimeUnit.SECONDS);
		assertTrue(completed, "모든 task는 시간 내에 완료되어야 합니다.");

		// then
		log.info("Results size: {}", results.size());
		assertEquals(numberOfUsers, results.size(), "결과 목록의 크기가 사용자 수와 일치해야 합니다");

		List<Integer> queueNumbers = results.stream()
			.map(QueueJoinResponseDto::sequence)
			.sorted()
			.toList();

		log.info("Queue numbers: {}", queueNumbers);

		for (int i = 0; i < numberOfUsers; i++) {
			assertEquals(i + 1, queueNumbers.get(i), "큐 번호가 순차적이어야 합니다");
		}

		executor.shutdown();
		assertTrue(executor.awaitTermination(1, TimeUnit.SECONDS), "Executor should terminate");
	}

	@Test
	@DisplayName("단일 사용자가 여러 번 대기열 등록 요청을 할 경우, 한 번만 처리된다.")
	public void joinQueue_singleUserMultipleRequests_processesOnlyOnce() throws InterruptedException {
		// given
		Long storeId = 1L;
		Long userId = 1L;
		Long ticketId = 1L;
		int numberOfRequests = 10;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);
		CountDownLatch latch = new CountDownLatch(numberOfRequests);
		List<QueueJoinResponseDto> results = Collections.synchronizedList(new ArrayList<>());

		doAnswer(invocation -> {
			Long invokedUserId = invocation.getArgument(1);
			String waitlistKey = "waitlist:1";
			if (redisTemplateForStoreQueueRedis.opsForSet().isMember(waitlistKey, invokedUserId.toString())) {
				log.info("User {} already in queue", invokedUserId);
				throw new GlobalException(ErrorCode.USER_ALREADY_IN_QUEUE);
			}
			redisTemplateForStoreQueueRedis.opsForSet().add(waitlistKey, invokedUserId.toString());
			redisTemplateForStoreQueueRedis.opsForValue().increment("waitlist:1:seq");
			log.info("Processing joinUserInQueue for userId: {}", invokedUserId);
			return null;
		}).when(helper).joinUserInQueue(eq(1L), eq(1L), eq(1L));

		when(helper.getUserQueuePosition(1L, 1L)).thenReturn(1);

		when(queueApplicationMapper.toQueueJoinResponseDto(1L, 1L, 1))
			.thenReturn(new QueueJoinResponseDto(1L, 1L, 1));

		// when
		for (int i = 0; i < numberOfRequests; i++) {
			executor.submit(() -> {
				try {
					QueueJoinServiceDto dto = new QueueJoinServiceDto(storeId, ticketId, userId);
					QueueJoinResponseDto response = service.joinQueue(dto);
					synchronized (results) {
						results.add(response);
					}
					log.info("Added result for userId: {} with ticketId: {} and position: {}",
						userId, ticketId, response.sequence());
				} catch (GlobalException e) {
					if (e.getErrorCode() == ErrorCode.USER_ALREADY_IN_QUEUE) {
						log.info("Ignored duplicate request for userId: {} due to USER_ALREADY_IN_QUEUE", userId);
					} else {
						log.error("Error during queue join for userId: {}", userId, e);
					}
				} catch (Exception e) {
					log.error("Unexpected error during queue join for userId: {}", userId, e);
				} finally {
					latch.countDown();
				}
			});
		}

		boolean completed = latch.await(5, TimeUnit.SECONDS);
		assertTrue(completed, "All tasks should complete within the timeout period");

		// then
		log.info("Results size: {}", results.size());
		assertEquals(1, results.size(), "단일 사용자의 요청은 한 번만 처리되어야 합니다");

		QueueJoinResponseDto result = results.get(0);
		assertEquals(1L, result.userId(), "userId는 1이어야 합니다");
		assertEquals(1, result.sequence(), "대기열 위치는 1이어야 합니다");

		Long waitlistSize = redisTemplateForStoreQueueRedis.opsForSet().size("waitlist:1");
		assertEquals(1L, waitlistSize, "waitlist:1에 단일 사용자만 존재해야 합니다");

		executor.shutdown();
		assertTrue(executor.awaitTermination(1, TimeUnit.SECONDS), "Executor should terminate");
	}
}