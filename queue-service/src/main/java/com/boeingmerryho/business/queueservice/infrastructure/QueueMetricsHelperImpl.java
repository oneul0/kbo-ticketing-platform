package com.boeingmerryho.business.queueservice.infrastructure;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class QueueMetricsHelperImpl {

	private final AtomicInteger waitingUsers = new AtomicInteger(0);
	private final Counter queueRequestCounter;

	public QueueMetricsHelperImpl(MeterRegistry meterRegistry) {
		Gauge.builder("queue.waiting.users", waitingUsers, AtomicInteger::get)
			.description("대기 중인 사용자 수")
			.register(meterRegistry);

		this.queueRequestCounter = Counter.builder("queue.request.count")
			.description("줄서기 요청 수")
			.register(meterRegistry);
	}

	public void incrementWaitingUsers() {
		waitingUsers.incrementAndGet();
	}

	public void decrementWaitingUsers() {
		waitingUsers.decrementAndGet();
	}

	public void countQueueRequest() {
		queueRequestCounter.increment();
	}
}

