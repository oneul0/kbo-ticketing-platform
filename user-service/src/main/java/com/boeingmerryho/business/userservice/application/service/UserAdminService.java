package com.boeingmerryho.business.userservice.application.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.dto.mapper.UserApplicationMapper;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminDeleteRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminDeleteRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.UserAdminFindResponseDto;
import com.boeingmerryho.business.userservice.application.dto.response.UserLoginResponseServiceDto;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.application.utils.jwt.JwtTokenProvider;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.UserSearchCriteria;
import com.boeingmerryho.business.userservice.domain.repository.CustomUserRepository;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.exception.UserException;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminRefreshTokenResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserLoginResponseDto;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {

	private final UserRepository userRepository;
	private final CustomUserRepository customUserRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisUtil redisUtil;
	private final UserHelper userHelper;

	@Value("${admin.key}")
	private String adminKey;
	@Value("${slack.code.ttl}")
	private Long SLACK_CODE_TTL;

	@Transactional
	public void registerUserAdmin(UserAdminRegisterRequestServiceDto requestServiceDto) {
		userHelper.validateRequiredField(requestServiceDto.username(), ErrorCode.USERNAME_NULL);
		userHelper.validateRequiredField(requestServiceDto.password(), ErrorCode.PASSWORD_NULL);
		userHelper.validateRequiredField(requestServiceDto.slackId(), ErrorCode.SLACKID_NULL);
		userHelper.validateRequiredField(requestServiceDto.key(), ErrorCode.ADMIN_REGISTER_KEY_IS_NULL);

		userHelper.emailVerify(requestServiceDto.username());
		userHelper.passwordVerify(requestServiceDto.password());
		userHelper.checkUsernameExists(requestServiceDto.username(), userRepository);

		if (!requestServiceDto.key().equals(adminKey)) {
			throw new UserException(ErrorCode.ADMIN_REGISTER_KEY_NOT_MATCH);
		}

		String encodedPassword = userHelper.encodePassword(requestServiceDto.password(),
			passwordEncoder);
		User user = User.builder()
			.username(requestServiceDto.username())
			.password(encodedPassword)
			.email(requestServiceDto.slackId())
			.role(UserRoleType.ADMIN)
			.build();

		//todo: 생성된 유저 반환하기
		userRepository.save(user);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "user", key = "#requestServiceDto.id()")
	public UserAdminFindResponseDto findUserAdmin(UserAdminFindRequestServiceDto requestServiceDto) {
		User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
		return userApplicationMapper.toUserAdminFindResponseDto(user);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "users")
	public Page<UserAdminSearchResponseDto> searchUsers(
		UserAdminSearchRequestServiceDto requestServiceDto, Pageable pageable) {
		Page<User> users = customUserRepository.findDynamicQuery(
			createUserSearchCriteria(requestServiceDto),
			pageable);
		return users.map(userApplicationMapper::toUserAdminSearchResponseDto);
	}

	@Transactional
	public UserAdminUpdateResponseDto updateUser(UserAdminUpdateRequestServiceDto requestServiceDto) {

		return null;
	}

	@Transactional
	public UserAdminUpdateResponseDto updateMe(UserAdminUpdateRequestServiceDto requestServiceDto) {

		return null;
	}

	@Transactional
	public UserAdminUpdateRoleResponseDto updateRoleUser(
		UserAdminUpdateRoleRequestServiceDto requestServiceDto) {
		User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
		userHelper.checkMasterRole(user);

		UserRoleType role = user.getRole();
		user.updateRoleType(requestServiceDto.newRole());

		User updatedUser = userHelper.findUserById(requestServiceDto.id(), userRepository);
		userHelper.updateRedisUserInfo(updatedUser, redisUtil);
		return userApplicationMapper.toUserAdminUpdateRoleResponseDto(
			requestServiceDto.id(), role, requestServiceDto.newRole());
	}

	@Transactional
	public void deleteRoleUser(UserAdminDeleteRoleRequestServiceDto requestServiceDto) {
		User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
		userHelper.checkMasterRole(user);

		user.deleteRoleType();

		User updatedUser = userHelper.findUserById(requestServiceDto.id(), userRepository);
		userHelper.updateRedisUserInfo(updatedUser, redisUtil);
	}

	@Transactional
	public void deleteUser(UserAdminDeleteRequestServiceDto requestServiceDto) {
		User user = userHelper.findUserById(requestServiceDto.id(), userRepository);
		user.softDelete(user.getId());

		String userInfoKey = "user:info:" + user.getId();
		String tokenKey = "user:token:" + user.getId();
		redisTemplate.delete(userInfoKey);
		redisTemplate.delete(tokenKey);
	}

	public UserSearchCriteria createUserSearchCriteria(UserAdminSearchRequestServiceDto requestDto) {
		return UserSearchCriteria.builder()
			.id(requestDto.id())
			.username(requestDto.username())
			.email(requestDto.email())
			.nickname(requestDto.nickname())
			.role(requestDto.role())
			.isDeleted(requestDto.isDeleted())
			.build();
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
		} else {
			redisTemplate.expire(blacklistKey, 1, TimeUnit.SECONDS);
		}

		if (userId != null) {
			String userInfoKey = "user:token:" + userId;
			redisTemplate.delete(userInfoKey);
		}
	}

	public UserLoginResponseDto loginUserAdmin(UserAdminLoginRequestServiceDto requestServiceDto) {
		if (!userRepository.existsByEmail(requestServiceDto.email())) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}

		User user = userHelper.findUserByEmail(requestServiceDto.email(), userRepository);
		userHelper.updateRedisUserInfo(user, redisUtil);
		Map<String, String> tokenMap = redisUtil.updateUserJwtToken(user.getId());

		UserLoginResponseServiceDto serviceDto = new UserLoginResponseServiceDto(
			tokenMap.get("accessToken"),
			tokenMap.get("refreshToken")
		);
		return userApplicationMapper.toUserLoginResponseDto(serviceDto);
	}

	@Transactional
	public void withdrawUser(UserAdminWithdrawRequestServiceDto requestServiceDto) {
	}

	@Transactional(readOnly = true)
	@Cacheable
	public UserAdminCheckEmailResponseDto checkEmail(UserAdminCheckEmailRequestServiceDto requestServiceDto) {
		return null;
	}

	public UserAdminRefreshTokenResponseDto refreshToken(UserAdminRefreshTokenRequestServiceDto requestServiceDto) {
		return null;
	}
}