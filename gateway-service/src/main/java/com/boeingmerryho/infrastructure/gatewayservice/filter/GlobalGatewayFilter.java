package com.boeingmerryho.infrastructure.gatewayservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

/**
 * 로깅, 요청 횟수 제한 적용 필터
 */
@Component
public class GlobalGatewayFilter implements GlobalFilter, Ordered {

	private static final Logger logger = LoggerFactory.getLogger(GlobalGatewayFilter.class);
	private static final Logger requestLogger = LoggerFactory.getLogger("requestLogger");
	private static final Logger suspiciousLogger = LoggerFactory.getLogger("suspiciousLogger");

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String requestId = UUID.randomUUID().toString();
		String clientIp = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
		if (clientIp == null) {
			clientIp = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
		}
		String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
		String method = exchange.getRequest().getMethod().name();
		String uri = exchange.getRequest().getURI().toString();

		MDC.put("requestId", requestId);
		MDC.put("ip", clientIp);
		MDC.put("userAgent", userAgent != null ? userAgent : "unknown");
		MDC.put("method", method);
		MDC.put("uri", uri);

		exchange.getRequest().mutate()
			.header("X-Request-Id", requestId)
			.header("X-Client-Ip", clientIp)
			.header("X-User-Agent", userAgent != null ? userAgent : "unknown")
			.header("X-Method", method)
			.header("X-Uri", uri)
			.build();

		requestLogger.info("Incoming request");

		long startTime = System.currentTimeMillis();

		return chain.filter(exchange)
			.doOnError(throwable -> {
				if (throwable.getMessage() != null && throwable.getMessage().contains("Rate limit exceeded")) {
					MDC.put("status", "429");
					suspiciousLogger.error("Rate limit exceeded", throwable);
				} else {
					MDC.put("status", "500");
					logger.error("Request failed", throwable);
				}
			})
			.then(Mono.fromRunnable(() -> {
				long duration = System.currentTimeMillis() - startTime;
				MDC.put("duration", String.valueOf(duration));

				String status = exchange.getResponse().getStatusCode() != null
					? String.valueOf(exchange.getResponse().getStatusCode().value())
					: "unknown";
				MDC.put("status", status);

				requestLogger.info("Request completed");
				MDC.clear();
			}));
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
