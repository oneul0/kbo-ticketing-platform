package com.boeingmerryho.infrastructure.gatewayservice.filter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.boeingmerryho.infrastructure.gatewayservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cloud.gateway.jwt.filter.enabled", havingValue = "true", matchIfMissing = true)
public class JwtAuthFilter implements GlobalFilter, Ordered {

	private static final List<String> EXCLUDED_PATHS = Arrays.asList(
		"/api/v1/users/register",
		"/admin/v1/users/register",
		"/api/v1/users/login",
		"/admin/v1/users/login",
		"/api/v1/users/check",
		"/admin/v1/users/check",
		"/admin/v1/users/refresh",
		"/api/v1/users/refresh"
	);

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${spring.cloud.gateway.secrets.secretKey}")
	private String SECRET_KEY;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange,
		org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

		log.info("JwtAuthFilter ");

		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();

		if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
			return chain.filter(exchange);
		}

		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("JWT 토큰이 없습니다.");
			throw new GlobalException(ErrorCode.JWT_NOT_FOUND);
		}

		try {
			String token = authHeader;
			if (authHeader.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			Claims claims = validateToken(token);
			log.debug("JWT 검증 성공: {}", claims);

			if (isTokenBlacklisted(token)) {
				log.error("JWT가 블랙리스트에 존재합니다.");
				throw new GlobalException(ErrorCode.JWT_BLACKLISTED);
			}

			ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
				.header("X-User-Id", claims.getSubject())
				.build();

			log.info("user id is set up : {}", modifiedRequest.getHeaders().getFirst("X-User-Id"));

			return chain.filter(exchange.mutate().request(modifiedRequest).build());

		} catch (ExpiredJwtException e) {
			log.error("JWT 만료됨: {}", e.getMessage());
			throw new GlobalException(ErrorCode.JWT_EXPIRED);
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
			log.error("잘못된 JWT: {}", e.getMessage());
			throw new GlobalException(ErrorCode.WRONG_JWT);
		} catch (Exception e) {
			log.error("JWT 검증 중 오류 발생: {}", e.getMessage());
			throw new GlobalException(ErrorCode.JWT_VERIFIED_FAIL);
		}
	}

	private Claims validateToken(String token) {
		return Jwts.parser()
			.setSigningKey(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
			.parseClaimsJws(token)
			.getBody();
	}

	private boolean isTokenBlacklisted(String token) {
		String blacklistKey = "blacklist:" + token;
		return redisTemplate.hasKey(blacklistKey);
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
