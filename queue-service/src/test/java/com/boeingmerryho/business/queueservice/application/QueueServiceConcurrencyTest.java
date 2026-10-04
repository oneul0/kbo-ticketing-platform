package com.boeingmerryho.business.queueservice.application;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.service.QueueService;
import com.boeingmerryho.business.queueservice.config.RedissonConfig;
import com.boeingmerryho.business.queueservice.config.aop.AopForTransaction;
import com.boeingmerryho.business.queueservice.config.aop.DistributedLockAop;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;
import com.boeingmerryho.business.queueservice.infrastructure.QueueMetricsHelperImpl;
import com.boeingmerryho.business.queueservice.infrastructure.QueueRedisHelperImpl;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/** Real Redis, production serializers, mapper, service and distributed-lock AOP.
 * Only the persistence collaborator (unused by join) is mocked. No external app config is loaded.
 */
@SpringJUnitConfig(QueueServiceConcurrencyTest.TestConfig.class)
@Testcontainers
class QueueServiceConcurrencyTest {
    private static final long STORE = 1L;
    private static final int REQUESTS = 10;

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:6.2.17-alpine")
        .withExposedPorts(6379)
        .withCommand("redis-server", "--requirepass", "testpass", "--save", "", "--appendonly", "no")
        .withStartupTimeout(Duration.ofSeconds(60))
        .withReuse(false);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.store-queue.host", redis::getHost);
        registry.add("spring.data.redis.store-queue.port", () -> redis.getMappedPort(6379));
        registry.add("spring.data.redis.store-queue.username", () -> "default");
        registry.add("spring.data.redis.store-queue.password", () -> "testpass");
    }

    @Configuration
    @EnableAspectJAutoProxy
    @EnableTransactionManagement
    @Import({RedissonConfig.class, QueueRedisHelperImpl.class, QueueService.class,
        QueueMetricsHelperImpl.class, DistributedLockAop.class, AopForTransaction.class})
    static class TestConfig {
        @Bean QueueApplicationMapper mapper() { return Mappers.getMapper(QueueApplicationMapper.class); }
        @Bean MeterRegistry meterRegistry() { return new SimpleMeterRegistry(); }
        @Bean(destroyMethod = "shutdown") org.springframework.jdbc.datasource.embedded.EmbeddedDatabase dataSource() {
            return new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(EmbeddedDatabaseType.H2).build();
        }
        @Bean PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Autowired QueueService service;
    @Autowired QueueRedisHelper redisHelper;
    @Autowired RedisTemplate<String, Object> redisTemplateForStoreQueueRedis;
    @Autowired MeterRegistry meters;
    @MockitoBean QueuePersistenceHelper persistenceHelper;
    private String queueKey;
    private double waitingBefore;
    private double requestsBefore;

    @BeforeEach
    void setUp() {
        // Connection comes exclusively from this class's disposable container, never application.yml.
        try (var connection = redisTemplateForStoreQueueRedis.getConnectionFactory().getConnection()) {
            connection.serverCommands().flushDb();
        }
        queueKey = redisHelper.getWaitlistInfoPrefix(STORE);
        redisTemplateForStoreQueueRedis.opsForValue().set("queue:availability:" + STORE, true);
        for (long user = 1; user <= REQUESTS; user++) {
            redisTemplateForStoreQueueRedis.opsForSet().add("queue:ticket:" + LocalDate.now(), Long.toString(user));
            redisTemplateForStoreQueueRedis.opsForValue().set("ticket:user:" + user, Long.toString(user));
        }
        waitingBefore = meters.get("queue.waiting.users").gauge().value();
        requestsBefore = meters.get("queue.request.count").counter().count();
    }

    @RepeatedTest(20)
    void distinctUsersHaveUniqueStoredSequencesAndMatchingResponses() throws Exception {
        var outcomes = concurrently(IntStream.rangeClosed(1, REQUESTS).mapToObj(i -> (long)i).toList(), false);
        report("distinct-service", outcomes);
        assertCounts(outcomes, REQUESTS, 0);
        assertEquals(REQUESTS, redisHelper.getTotalQueueSize(queueKey));
        assertEquals(REQUESTS, sequenceCounter());
        Set<Integer> sequences = new HashSet<>();
        Set<Object> members = new HashSet<>();
        for (var outcome : outcomes) {
            var response = outcome.response();
            assertEquals(STORE, response.storeId());
            assertEquals(outcome.user(), response.userId());
            assertEquals(redisHelper.getUserQueuePosition(STORE, outcome.user()), response.sequence());
            assertEquals(redisHelper.getUserSequencePosition(STORE, outcome.user()), response.sequence());
            sequences.add(response.sequence());
            members.add(Long.toString(outcome.user()));
        }
        assertEquals(new HashSet<>(IntStream.rangeClosed(1, REQUESTS).boxed().toList()), sequences);
        assertEquals(members, redisTemplateForStoreQueueRedis.opsForZSet().range(queueKey, 0, -1));
        assertMetrics(REQUESTS);
        assertTtl();
    }

    @RepeatedTest(20)
    void sameUserIsAcceptedOnceAndOtherRequestsAreExplicitlyRejected() throws Exception {
        var outcomes = concurrently(java.util.Collections.nCopies(REQUESTS, 1L), false);
        report("duplicate-service", outcomes);
        assertCounts(outcomes, 1, REQUESTS - 1);
        var response = outcomes.stream().filter(o -> o.response() != null).findFirst().orElseThrow().response();
        assertEquals(new QueueJoinResponseDto(STORE, 1L, 1), response);
        assertSingleStoredUser();
        assertMetrics(1);
        assertTtl();
    }

    @RepeatedTest(20)
    void helperRejectsDuplicatesAtomicallyWithoutServiceLock() throws Exception {
        var outcomes = concurrently(java.util.Collections.nCopies(REQUESTS, 1L), true);
        report("duplicate-helper", outcomes);
        assertCounts(outcomes, 1, REQUESTS - 1);
        assertSingleStoredUser();
        assertTtl();
    }

    @Test
    void duplicatePreservesOriginalScoreRankCounterAndTtl() throws Exception {
        service.joinQueue(new QueueJoinServiceDto(STORE, 1L, 1L));
        service.joinQueue(new QueueJoinServiceDto(STORE, 2L, 2L));
        redisTemplateForStoreQueueRedis.expire(queueKey, Duration.ofMinutes(10));
        redisTemplateForStoreQueueRedis.expire(queueKey + ":seq", Duration.ofMinutes(10));
        GlobalException error = assertThrows(GlobalException.class,
            () -> service.joinQueue(new QueueJoinServiceDto(STORE, 1L, 1L)));
        assertSame(ErrorCode.USER_ALREADY_IN_QUEUE, error.getErrorCode());
        assertEquals(1, redisHelper.getUserSequencePosition(STORE, 1L));
        assertEquals(1, redisHelper.getUserQueuePosition(STORE, 1L));
        assertEquals(2, redisHelper.getUserQueuePosition(STORE, 2L));
        assertEquals(2, sequenceCounter());
        assertEquals(2, redisHelper.getTotalQueueSize(queueKey));
        assertTrue(redisTemplateForStoreQueueRedis.getExpire(queueKey) <= 600);
        assertTrue(redisTemplateForStoreQueueRedis.getExpire(queueKey + ":seq") <= 600);
        assertTtl();
        assertMetrics(2);
    }

    @Test
    void removalAllowsNewSequenceWithoutChangingOtherUsers() throws Exception {
        service.joinQueue(new QueueJoinServiceDto(STORE, 1L, 1L));
        service.joinQueue(new QueueJoinServiceDto(STORE, 2L, 2L));
        assertTrue(redisHelper.removeUserFromQueue(STORE, 1L));
        var response = service.joinQueue(new QueueJoinServiceDto(STORE, 1L, 1L));
        assertEquals(2, response.sequence()); // response is current rank, not immutable sequence
        assertEquals(3, redisHelper.getUserSequencePosition(STORE, 1L));
        assertEquals(1, redisHelper.getUserQueuePosition(STORE, 2L));
        assertEquals(3, sequenceCounter());
        assertEquals(2, redisHelper.getTotalQueueSize(queueKey));
    }

    @Test
    void businessValidationErrorSurvivesLockAspect() {
        redisTemplateForStoreQueueRedis.opsForValue().set("queue:availability:" + STORE, false);
        var error = assertThrows(GlobalException.class,
            () -> service.joinQueue(new QueueJoinServiceDto(STORE, 1L, 1L)));
        assertSame(ErrorCode.STORE_IS_NOT_ACTIVATED, error.getErrorCode());
        assertFalse(redisTemplateForStoreQueueRedis.hasKey(queueKey));
        assertFalse(redisTemplateForStoreQueueRedis.hasKey(queueKey + ":seq"));
        assertMetrics(0);
    }

    private void assertSingleStoredUser() {
        assertEquals(Set.of("1"), redisTemplateForStoreQueueRedis.opsForZSet().range(queueKey, 0, -1));
        assertEquals(1, sequenceCounter());
        assertEquals(1, redisHelper.getUserSequencePosition(STORE, 1L));
        assertEquals(1, redisHelper.getUserQueuePosition(STORE, 1L));
    }

    private int sequenceCounter() {
        return Integer.parseInt(redisTemplateForStoreQueueRedis.opsForValue().get(queueKey + ":seq").toString());
    }

    private void assertTtl() {
        for (String key : List.of(queueKey, queueKey + ":seq")) {
            long ttl = redisTemplateForStoreQueueRedis.getExpire(key);
            assertTrue(ttl > 0 && ttl <= 86400, key + " TTL=" + ttl);
        }
    }

    private void assertMetrics(int accepted) {
        assertEquals(waitingBefore + accepted, meters.get("queue.waiting.users").gauge().value());
        assertEquals(requestsBefore + accepted, meters.get("queue.request.count").counter().count());
    }

    private record Outcome(long user, QueueJoinResponseDto response, Throwable error) { }

    private List<Outcome> concurrently(List<Long> users, boolean helperOnly) throws Exception {
        var executor = Executors.newFixedThreadPool(users.size());
        var ready = new CountDownLatch(users.size());
        var start = new CountDownLatch(1);
        var futures = new ArrayList<Future<Outcome>>();
        try {
            for (long user : users) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(10, TimeUnit.SECONDS)) throw new TimeoutException("start barrier");
                    try {
                        if (helperOnly) {
                            redisHelper.joinUserInQueue(STORE, user, user);
                            return new Outcome(user, new QueueJoinResponseDto(STORE, user,
                                redisHelper.getUserQueuePosition(STORE, user)), null);
                        }
                        return new Outcome(user, service.joinQueue(new QueueJoinServiceDto(STORE, user, user)), null);
                    } catch (Exception error) {
                        return new Outcome(user, null, error);
                    }
                }));
            }
            assertTrue(ready.await(10, TimeUnit.SECONDS), "all workers must reach the start barrier");
            long started = System.nanoTime();
            start.countDown();
            var outcomes = new ArrayList<Outcome>();
            for (var future : futures) outcomes.add(future.get(15, TimeUnit.SECONDS));
            System.out.printf("QUEUE_BATCH helperOnly=%s distinctUsers=%d requests=%d elapsedMs=%.3f%n",
                helperOnly, users.stream().distinct().count(), users.size(), (System.nanoTime() - started) / 1_000_000.0);
            return outcomes;
        } finally {
            start.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS), "workers must terminate");
        }
    }

    private boolean isDuplicate(Outcome outcome) {
        return outcome.error() instanceof GlobalException error && error.getErrorCode() == ErrorCode.USER_ALREADY_IN_QUEUE;
    }

    private void assertCounts(List<Outcome> outcomes, int accepted, int rejected) {
        assertEquals(REQUESTS, outcomes.size());
        assertEquals(accepted, outcomes.stream().filter(o -> o.response() != null).count(), outcomes.toString());
        assertEquals(rejected, outcomes.stream().filter(this::isDuplicate).count(), outcomes.toString());
        assertEquals(0, outcomes.stream().filter(o -> o.error() != null && !isDuplicate(o)).count(), outcomes.toString());
    }

    private void report(String scenario, List<Outcome> outcomes) {
        long accepted = outcomes.stream().filter(o -> o.response() != null).count();
        long rejected = outcomes.stream().filter(this::isDuplicate).count();
        System.out.printf("QUEUE_RESULT scenario=%s accepted=%d rejected=%d failed=%d size=%d counter=%d members=%s%n",
            scenario, accepted, rejected, outcomes.size() - accepted - rejected,
            redisHelper.getTotalQueueSize(queueKey), sequenceCounter(),
            redisTemplateForStoreQueueRedis.opsForZSet().rangeWithScores(queueKey, 0, -1));
    }
}
