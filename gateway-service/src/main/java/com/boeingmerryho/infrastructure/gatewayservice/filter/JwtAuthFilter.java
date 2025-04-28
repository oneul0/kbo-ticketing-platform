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

import com.boeingmerryho.infrastructure.gatewayservice.exception.JwtErrorCode;

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

		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();

		if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
			log.debug("JWT 검증 제외 경로: {}", path);
			return chain.filter(exchange);
		}

		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		log.info("authHeader : {}", authHeader);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("JWT Token not found, path: {}", path);
			throw new GlobalException(JwtErrorCode.JWT_NOT_FOUND);
		}

		try {
			String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

			if (isTokenBlacklisted(token)) {
				log.error("JWT blacklisted, path: {}, token: {}", path, maskToken(token));
				throw new GlobalException(JwtErrorCode.JWT_BLACKLISTED);
			}

			Claims claims = validateToken(token);
			log.info("JWT validation success, userId: {}, path: {}", claims.getSubject(), path);

			ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
				.header("X-User-Id", claims.getSubject())
				.build();

			return chain.filter(exchange.mutate().request(modifiedRequest).build());

		} catch (ExpiredJwtException e) {
			log.error("JWT expired, path: {}, error: {}", path, e.getMessage());
			throw new GlobalException(JwtErrorCode.JWT_EXPIRED);
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
			log.error("Wrong JWT, path: {}, error: {}", path, e.getMessage());
			throw new GlobalException(JwtErrorCode.WRONG_JWT);
		} catch (GlobalException e) {
			log.error("JWT Exception, path: {}, errorCode: {}", path, e.getErrorCode());
			throw e;
		} catch (Exception e) {
			log.error("JWT validation failed, path: {}, error: {}", path, e.getMessage());
			throw new GlobalException(JwtErrorCode.JWT_VERIFIED_FAIL);
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

	private String maskToken(String token) {
		return token.length() > 10 ? token.substring(0, 5) + "..." + token.substring(token.length() - 5) : token;
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
