package com.boeingmerryho.business.userservice.application.utils.jwt;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	public String generateAccessToken(Long userId) {
		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateRefreshToken(Long userId) {
		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
			.compact();
	}

	public long getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}

	public Claims parseJwtToken(String token) {
		try {
			return Jwts.parser()
				.setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes()))
				.parseClaimsJws(token)
				.getBody();
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid JWT token");
		}
	}

	public long calculateTtlMillis(Date expiration) {
		long currentTimeMillis = System.currentTimeMillis();
		long expirationTimeMillis = expiration.getTime();
		return Math.max(0, expirationTimeMillis - currentTimeMillis); // 음수가 되지 않도록
	}

	public boolean validateRefreshToken(String token) {
		try {
			if (token == null || token.trim().isEmpty()) {
				log.warn("Token is null or empty");
				throw new GlobalException(ErrorCode.JWT_INVALID);
			}
			Jwts.parser()
				.setSigningKey(secretKey.getBytes())
				.parseClaimsJws(token);
			log.debug("Refresh token validated successfully");
			return true;
		} catch (ExpiredJwtException e) {
			log.warn("Expired refresh token: {}", e.getMessage());
			throw new GlobalException(ErrorCode.JWT_EXPIRED);
		} catch (MalformedJwtException e) {
			log.warn("Malformed JWT token: {}", e.getMessage());
			throw new GlobalException(ErrorCode.MALFORMED_JWT);
		} catch (JwtException e) {
			log.warn("JWT validation error: {}", e.getMessage());
			throw new GlobalException(ErrorCode.JWT_INVALID);
		} catch (IllegalArgumentException e) {
			log.warn("Illegal argument for JWT: {}", e.getMessage());
			throw new GlobalException(ErrorCode.JWT_INVALID);
		}
	}

	public String getUserIdFromToken(String token) {
		return Jwts.parser()
			.setSigningKey(secretKey.getBytes())
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}
}
