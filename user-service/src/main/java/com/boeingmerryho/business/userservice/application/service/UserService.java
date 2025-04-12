package com.boeingmerryho.business.userservice.application.service;

import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.dto.mapper.UserApplicationMapper;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserEmailVerificationCheckRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserEmailVerificationRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.inner.UserTokenResult;
import com.boeingmerryho.business.userservice.application.dto.response.other.UserLoginResponseServiceDto;
import com.boeingmerryho.business.userservice.application.utils.mail.EmailService;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserFindResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserLoginResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserRefreshTokenResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserVerificationResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;
	private final RedisTemplate<String, Object> redisTemplate;
	private final UserHelper userHelper;
	private final EmailService emailService;

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
		User user = userHelper.findUserByEmail(dto.email(), userRepository);
		userHelper.updateRedisUserInfo(user);

		Map<String, String> tokenMap = userHelper.updateUserJwtTokenRedis(user.getId());
		UserLoginResponseServiceDto serviceDto = UserLoginResponseServiceDto.fromTokens(
			tokenMap.get("accessToken"),
			tokenMap.get("refreshToken")
		);

		userHelper.getNotifyLoginResponse(user.getId());

		return userApplicationMapper.toUserLoginResponseDto(serviceDto);
	}

	public void logoutUser(UserLogoutRequestServiceDto dto) {
		Long userId = dto.id();

		UserTokenResult result = userHelper.getUserTokenFromRedis(userId);
		String accessToken = (String)result.token().get("accessToken");
		userHelper.blacklistToken(accessToken);

		userHelper.deleteKeyFromRedis(result.tokenKey());
		
		userHelper.removeUserMembershipInfoFromRedis(userId);
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

		userHelper.updateRedisUserInfo(user);

		return userApplicationMapper.toUserAdminUpdateResponseDto(user.getId());
	}

	@Transactional
	public Long withdrawUser(UserWithdrawRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);
		user.softDelete(user.getId());

		userHelper.clearRedisUserData(user.getId());

		return user.getId();
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "emailCheck", key = "'user:' + #dto.email()")
	public UserCheckEmailResponseDto checkEmail(UserCheckEmailRequestServiceDto dto) {
		Boolean idEmailDuplicated = userRepository.existsByEmail(dto.email());
		return userApplicationMapper.toUserCheckEmailResponseDto(idEmailDuplicated);
	}

	public UserRefreshTokenResponseDto refreshToken(UserRefreshTokenRequestServiceDto dto) {
		String refreshToken = dto.refreshToken();

		log.debug("refresh requested refreshToken : {}", refreshToken);
		userHelper.isValidRefreshToken(refreshToken);

		Long userId = userHelper.getUserIdFromToken(refreshToken);

		userHelper.isEqualStoredRefreshToken(userId, refreshToken);

		String newAccessToken = userHelper.generateAccessToken(userId);

		return new UserRefreshTokenResponseDto(newAccessToken);
	}

	public UserVerificationResponseDto sendVerificationCode(
		UserEmailVerificationRequestServiceDto dto) {
		userHelper.checkDuplicatedVerificationRequest(dto.email());
		userHelper.verifyEmailFormat(dto.email());
		String code = userHelper.generateVerificationCode();
		userHelper.storeVerificationCode(dto.email(), code);
		emailService.sendVerificationEmail(dto.email(), code);
		return userApplicationMapper.toUserVerificationResponseDto(dto.email()).success("인증 메일 발송 성공");
	}

	public UserVerificationResponseDto verifyCode(UserEmailVerificationCheckRequestServiceDto dto) {
		String storedCode = userHelper.getVerificationCode(dto.email());
		if (storedCode == null || !storedCode.equals(dto.code())) {
			throw new GlobalException(ErrorCode.VERIFICATION_FAIL);
		}
		userHelper.removeVerificationCode(dto.email());

		return UserVerificationResponseDto.success("메일 인증 성공");
	}
}