package com.boeingmerryho.infrastructure.gatewayservice.filter.util;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class IpKeyResolver implements KeyResolver {
	@Override
	public Mono<String> resolve(ServerWebExchange exchange) {
		String clientIp = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
		if (clientIp == null) {
			clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
		}
		return Mono.just(clientIp);
	}
}
