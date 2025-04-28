package com.boeingmerryho.business.userservice.application.service;

import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.UserJwtHelper;
import com.boeingmerryho.business.userservice.application.UserVerificationHelper;
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
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
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
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final UserHelper userHelper;
	private final UserJwtHelper userJwtHelper;
	private final UserVerificationHelper userVerificationHelper;
	private final RedisUtil redisUtil;
	private final EmailService emailService;

	@Transactional
	@Counted(value = "user.register", description = "회원가입 요청 횟수")
	public Long registerUser(UserRegisterRequestServiceDto dto) {
		userHelper.validateRegisterRequest(dto);

		User user = User.withDefaultRole(
			dto.username(),
			userHelper.encodePassword(dto.password()),
			dto.email(),
			dto.nickname(),
			dto.birth()
		);

		return userRepository.save(user).getId();
	}

	public UserLoginResponseDto loginUser(UserLoginRequestServiceDto dto) {
		User user = userHelper.findUserByEmail(dto.email());

		userHelper.countLoginAttempt(user.getId());

		userHelper.validatePassword(dto.password(), user.getPassword());

		try {
			redisUtil.updateUserInfo(user);

			Map<String, String> tokenMap = redisUtil.updateUserJwtToken(user.getId());
			UserLoginResponseServiceDto serviceDto = UserLoginResponseServiceDto.fromTokens(
				tokenMap.get("accessToken"),
				tokenMap.get("refreshToken")
			);

			userVerificationHelper.getNotifyLoginResponse(user.getId());

			return userApplicationMapper.toUserLoginResponseDto(serviceDto);
		} catch (Exception e) {
			userHelper.countLoginFailure(user.getId());
			redisUtil.rollbackUserInfo(user.getId());
			redisUtil.rollbackUserJwtToken(user.getId());

			throw new GlobalException(ErrorCode.LOGIN_FAILED);
		}
	}

	public void logoutUser(UserLogoutRequestServiceDto dto) {
		Long userId = dto.id();

		UserTokenResult result = userHelper.getUserTokenFromRedis(userId);
		String accessToken = (String)result.token().get("accessToken");
		redisUtil.blacklistToken(accessToken);

		redisUtil.deleteFromRedisByKey(result.tokenKey());

		redisUtil.clearRedisUserData(userId);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "user", key = "'user:' + #dto.id()")
	public UserFindResponseDto findUser(UserFindRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id());
		return userApplicationMapper.toUserFindResponseDto(user);
	}

	@Transactional
	public UserAdminUpdateResponseDto updateMe(UserUpdateRequestServiceDto dto) {

		User user = userHelper.findUserById(dto.id());

		if (!userHelper.isEmpty(dto.password())) {
			String encodedPassword = userHelper.encodePassword(dto.password());
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

		redisUtil.updateUserInfo(user);

		return userApplicationMapper.toUserAdminUpdateResponseDto(user.getId());
	}

	@Transactional
	@Counted(value = "user.withdraw", description = "회원탈퇴 요청 횟수")
	public Long withdrawUser(UserWithdrawRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id());
		user.softDelete(user.getId());

		redisUtil.clearRedisUserData(user.getId());

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
		userJwtHelper.isValidRefreshToken(refreshToken);

		Long userId = userJwtHelper.getUserIdFromToken(refreshToken);

		redisUtil.isEqualStoredRefreshToken(userId, refreshToken);

		String newAccessToken = userJwtHelper.generateAccessToken(userId);

		return new UserRefreshTokenResponseDto(newAccessToken);
	}

	public UserVerificationResponseDto sendVerificationCode(
		UserEmailVerificationRequestServiceDto dto) {
		userVerificationHelper.checkDuplicatedVerificationRequest(dto.email());
		userHelper.verifyEmailFormat(dto.email());
		String code = userVerificationHelper.generateVerificationCode();
		userVerificationHelper.storeVerificationCode(dto.email(), code);
		emailService.sendVerificationEmail(dto.email(), code);
		return userApplicationMapper.toUserVerificationResponseDto(dto.email()).success("인증 메일 발송 성공");
	}

	public UserVerificationResponseDto verifyCode(UserEmailVerificationCheckRequestServiceDto dto) {
		String storedCode = userVerificationHelper.getVerificationCode(dto.email());
		if (storedCode == null || !storedCode.equals(dto.code())) {
			throw new GlobalException(ErrorCode.VERIFICATION_FAIL);
		}
		userVerificationHelper.removeVerificationCode(dto.email());

		return UserVerificationResponseDto.success("메일 인증 성공");
	}
}