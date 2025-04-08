package com.boeingmerryho.business.userservice.application.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.dto.mapper.UserApplicationMapper;
import com.boeingmerryho.business.userservice.application.dto.request.UserCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.UserLoginResponseServiceDto;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.application.utils.jwt.JwtTokenProvider;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.exception.UserException;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserFindResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserLoginResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserRefreshTokenResponseDto;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisUtil redisUtil;
	private final RedisTemplate<String, Object> redisTemplate;
	private final UserHelper userHelper;

	@Transactional
	public void registerUser(UserRegisterRequestServiceDto requestServiceDto) {

		String encodedPassword = userHelper.encodePassword(requestServiceDto.password(),
			passwordEncoder);
		User user = User.builder()
			.username(requestServiceDto.username())
			.password(encodedPassword)
			.email(requestServiceDto.slackId())
			.build();

		userRepository.save(user);
	}

	public UserLoginResponseDto loginUser(UserLoginRequestServiceDto requestServiceDto) {
		if (!userRepository.existsByEmail(requestServiceDto.username())) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}

		User user = userHelper.findUserByEmail(requestServiceDto.username(), userRepository);
		redisUtil.updateUserInfo(user);
		Map<String, String> tokenMap = redisUtil.updateUserJwtToken(user.getId());

		UserLoginResponseServiceDto serviceDto = new UserLoginResponseServiceDto(
			tokenMap.get("accessToken"),
			tokenMap.get("refreshToken")
		);
		return userApplicationMapper.toUserLoginResponseDto(serviceDto);
	}

	public void logoutUser(UserLogoutRequestServiceDto requestServiceDto) {
		Long userId = requestServiceDto.id();
		String tokenKey = "user:token:" + userId;
		if (!redisTemplate.hasKey(tokenKey)) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}
		Map<Object, Object> token = redisTemplate.opsForHash().entries(tokenKey);
		String accessToken = (String)token.get("accessToken");

		if (token == null || token.isEmpty()) {
			throw new UserException(ErrorCode.JWT_REQUIRED);
		}

		Claims claims = jwtTokenProvider.parseJwtToken(accessToken);
		long ttlMillis = jwtTokenProvider.calculateTtlMillis(claims.getExpiration());

		String blacklistKey = "blacklist:" + accessToken;
		redisTemplate.opsForValue().set(blacklistKey, "blacklisted");

		if (ttlMillis > 0) {
			redisTemplate.expire(blacklistKey, ttlMillis, TimeUnit.MILLISECONDS);
			log.debug("블랙리스트에 추가된 토큰 : {} TTL: {} ms", token, ttlMillis);
		} else {
			redisTemplate.expire(blacklistKey, 1, TimeUnit.SECONDS);
			log.debug("토큰 {} 은 이미 만료되어 최소 TTL로 설정", token);
		}

		if (userId != null) {
			String userInfoKey = "user:token:" + userId;
			redisTemplate.delete(userInfoKey);
		}
	}

	public UserFindResponseDto findUser(UserFindRequestServiceDto requestServiceDto) {
		if (!requestServiceDto.id().equals(requestServiceDto.id())) {
			throw new UserException(ErrorCode.USER_NOT_MATCH);
		}

		User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
		return userApplicationMapper.toUserFindResponseDto(user);
	}

	@Transactional
	public UserAdminUpdateResponseDto updateUser(UserUpdateRequestServiceDto requestServiceDto) {

		return null;
	}

	@Transactional
	public void deleteUser(UserWithdrawRequestServiceDto requestServiceDto) {
	}

	@Transactional(readOnly = true)
	@Cacheable
	public UserCheckEmailResponseDto checkEmail(UserCheckEmailRequestServiceDto requestServiceDto) {
		return null;
	}

	public UserRefreshTokenResponseDto refreshToken(UserRefreshTokenRequestServiceDto requestServiceDto) {
		return null;
	}
}