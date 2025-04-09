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

	private static final String USER_TOKEN_PREFIX = "user:token:";
	private static final String BLACKLIST_PREFIX = "blacklist:";

	private final UserRepository userRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisUtil redisUtil;
	private final RedisTemplate<String, Object> redisTemplate;
	private final UserHelper userHelper;

	@Transactional
	public Long registerUser(UserRegisterRequestServiceDto dto) {
		userHelper.validateRegisterRequest(dto, userRepository);

		User user = User.withDefaultRole(
			dto.username(),
			userHelper.encodePassword(dto.password(), passwordEncoder),
			dto.email(),
			dto.nickname(),
			dto.birth()
		);

		return userRepository.save(user).getId();
	}

	public UserLoginResponseDto loginUser(UserLoginRequestServiceDto dto) {
		User user = userHelper.findUserByEmail(dto.username(), userRepository);
		userHelper.updateRedisUserInfo(user, redisUtil);

		Map<String, String> tokenMap = redisUtil.updateUserJwtToken(user.getId());
		UserLoginResponseServiceDto serviceDto = UserLoginResponseServiceDto.fromTokens(
			tokenMap.get("accessToken"),
			tokenMap.get("refreshToken")
		);
		return userApplicationMapper.toUserLoginResponseDto(serviceDto);
	}

	public void logoutUser(UserLogoutRequestServiceDto dto) {
		String tokenKey = USER_TOKEN_PREFIX + dto.id();
		Map<Object, Object> token = getUserTokenFromRedis(tokenKey);
		String accessToken = extractAccessToken(token);

		blacklistToken(accessToken);
		redisTemplate.delete(tokenKey);
	}

	private Map<Object, Object> getUserTokenFromRedis(String tokenKey) {
		if (!redisTemplate.hasKey(tokenKey)) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}
		Map<Object, Object> token = redisTemplate.opsForHash().entries(tokenKey);
		if (token == null || token.isEmpty()) {
			throw new UserException(ErrorCode.JWT_REQUIRED);
		}
		return token;
	}

	private String extractAccessToken(Map<Object, Object> token) {
		return (String)token.get("accessToken");
	}

	private void blacklistToken(String accessToken) {
		Claims claims = jwtTokenProvider.parseJwtToken(accessToken);
		long ttlMillis = jwtTokenProvider.calculateTtlMillis(claims.getExpiration());
		String blacklistKey = BLACKLIST_PREFIX + accessToken;

		redisTemplate.opsForValue().set(blacklistKey, "blacklisted");
		redisTemplate.expire(blacklistKey, Math.max(ttlMillis, 1), TimeUnit.MILLISECONDS);
		log.debug("Token {} blacklisted with TTL: {} ms", accessToken, ttlMillis);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "user", key = "'user:' + #dto.id()")
	public UserFindResponseDto findUser(UserFindRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);
		return userApplicationMapper.toUserFindResponseDto(user);
	}

	@Transactional
	public UserAdminUpdateResponseDto updateMe(UserUpdateRequestServiceDto dto) {

		User user = userHelper.findUserById(dto.id(), userRepository);

		if (!userHelper.isEmpty(dto.password())) {
			String encodedPassword = userHelper.encodePassword(dto.password(), passwordEncoder);
			user.updatePassword(encodedPassword);
		}
		if (!userHelper.isEmpty(dto.username())) {
			user.updateUsername(dto.username());
		}
		if (!userHelper.isEmpty(dto.nickname())) {
			user.updateNickname(dto.nickname());
		}
		if (dto.birth() != null) {
			user.updateBirth(dto.birth());
		}

		userHelper.updateRedisUserInfo(user, redisUtil);

		return userApplicationMapper.toUserAdminUpdateResponseDto(user.getId());
	}

	@Transactional
	public void withdrawUser(UserWithdrawRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);
		user.softDelete(user.getId());

		clearRedisUserData(user.getId());
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "emailCheck", key = "'user:' + #dto.email()")
	public UserCheckEmailResponseDto checkEmail(UserCheckEmailRequestServiceDto dto) {
		Boolean idEmailDuplicated = userRepository.existsByEmail(dto.email());
		return userApplicationMapper.toUserCheckEmailResponseDto(idEmailDuplicated);
	}

	public UserRefreshTokenResponseDto refreshToken(UserRefreshTokenRequestServiceDto dto) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}