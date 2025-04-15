package com.boeingmerryho.business.userservice.infrastructure.helper;

import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.application.UserVerificationHelper;
import com.boeingmerryho.business.userservice.application.dto.request.feign.LoginSuccessRequest;
import com.boeingmerryho.business.userservice.application.feign.MembershipClient;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.exception.ErrorCode;

import feign.FeignException;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserVerificationHelperImpl implements UserVerificationHelper {

	private static final String VERIFICATION_PREFIX = "verification:email:";

	private final RedisUtil redisUtil;
	private final MembershipClient membershipClient;

	@Override
	public String generateVerificationCode() {
		return String.format("%06d", new Random().nextInt(1000000));
	}

	@Override
	public void storeVerificationCode(String email, String code) {
		String key = VERIFICATION_PREFIX + email;
		redisUtil.setTtlAndOpsForValueRedis(key, code, 5L);
	}

	@Override
	public String getVerificationCode(String email) {
		String key = VERIFICATION_PREFIX + email;
		return redisUtil.getOpsForValue(key);
	}

	@Override
	public void removeVerificationCode(String email) {
		String key = VERIFICATION_PREFIX + email;
		redisUtil.deleteFromRedisByKey(key);
	}

	@Override
	public void checkDuplicatedVerificationRequest(String email) {
		String key = VERIFICATION_PREFIX + email;
		if (redisUtil.hsaKeyInRedis(key)) {
			throw new GlobalException(ErrorCode.VERIFICATION_ALREADY_SENT);
		}
	}

	@Override
	public String getNotifyLoginResponse(Long id) {
		LoginSuccessRequest request = new LoginSuccessRequest(id);
		try {
			ResponseEntity<String> response = membershipClient.notifyLogin(request);
			return response.getBody();
		} catch (FeignException e) {
			if (e.status() >= 400 && e.status() < 500) {
				throw new GlobalException(ErrorCode.MEMBERSHIP_INFO_SETTING_FAIL);
			} else {
				throw new GlobalException(ErrorCode.MEMBERSHIP_FEIGN_REQUEST_FAIL);
			}
		} catch (Exception e) {
			throw new GlobalException(ErrorCode.MEMBERSHIP_FEIGN_REQUEST_FAIL);
		}
	}

}