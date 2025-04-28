package com.boeingmerryho.infrastructure.gatewayservice.filter;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 로깅, 요청 횟수 제한 적용 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalGatewayFilter implements GlobalFilter, Ordered {

	private final MeterRegistry meterRegistry;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String requestId = UUID.randomUUID().toString();
		String clientIp = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
		if (clientIp == null) {
			clientIp = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
		}
		String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
		String method = exchange.getRequest().getMethod().name();
		String uri = exchange.getRequest().getURI().getPath();

		var requestCounter = meterRegistry.counter(
			"gateway_requests_total",
			"path", uri,
			"method", method
		);

		var requestTimer = meterRegistry.timer(
			"gateway_request_duration",
			"path", uri,
			"method", method
		);

		requestCounter.increment();

		MDC.put("requestId", requestId);
		MDC.put("ip", clientIp);
		MDC.put("userAgent", userAgent != null ? userAgent : "unknown");
		MDC.put("method", method);
		MDC.put("uri", uri);

		ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
			.header("X-Request-Id", requestId)
			.header("X-Client-Ip", clientIp)
			.header("X-User-Agent", userAgent != null ? userAgent : "unknown")
			.header("X-Method", method)
			.header("X-Uri", uri)
			.build();

		ServerWebExchange mutatedExchange = exchange.mutate()
			.request(mutatedRequest)
			.build();

		log.info("Incoming request");

		long startTime = System.currentTimeMillis();

		return chain.filter(mutatedExchange)
			.doOnError(throwable -> {
				if (throwable.getMessage() != null && throwable.getMessage().contains("Rate limit exceeded")) {
					MDC.put("status", "429");
					log.error("Rate limit exceeded", throwable);
				} else {
					MDC.put("status", "500");
					log.error("Request failed", throwable);
				}
			})
			.then(Mono.fromRunnable(() -> {
				long duration = System.currentTimeMillis() - startTime;
				MDC.put("duration", String.valueOf(duration));

				String status = exchange.getResponse().getStatusCode() != null
					? String.valueOf(exchange.getResponse().getStatusCode().value())
					: "unknown";
				MDC.put("status", status);

				log.info("Request completed");

				requestTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);

				MDC.clear();
			}));
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
