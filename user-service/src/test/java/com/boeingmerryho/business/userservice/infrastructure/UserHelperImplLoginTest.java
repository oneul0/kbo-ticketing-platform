package com.boeingmerryho.business.userservice.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;

import com.boeingmerryho.business.userservice.application.dto.request.feign.LoginSuccessRequest;
import com.boeingmerryho.business.userservice.application.feign.MembershipClient;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.infrastructure.helper.UserHelperImpl;

import feign.FeignException;
import feign.Request;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;

@ExtendWith(MockitoExtension.class)
class UserHelperImplTest {

	@InjectMocks
	private UserHelperImpl userHelper;

	@Mock
	private MembershipClient membershipClient;

	@Test
	@Description("로그인 성공 시 정상적으로 응답을 반환한다")
	void should_return_response_when_login_successful() {
		Long userId = 1L;
		when(membershipClient.notifyLogin(any()))
			.thenReturn(ResponseEntity.ok("success"));

		String result = userHelper.getNotifyLoginResponse(userId);

		assertEquals("success", result);
	}

	@Test
	@Description("로그인 요청 응답이 400일 경우 MEMBERSHIP_INFO_SETTING_FAIL 예외 발생")
	void should_throw_MEMBERSHIP_INFO_SETTING_FAIL_when_login_request_returns_400() {
		Long userId = 1L;

		FeignException feignException = new FeignException.BadRequest(
			"Bad Request",
			Request.create(Request.HttpMethod.POST, "/membership/user/login", Map.of(), null, null, null),
			null, null
		);

		when(membershipClient.notifyLogin(any(LoginSuccessRequest.class)))
			.thenThrow(feignException);

		GlobalException exception = assertThrows(GlobalException.class, () -> {
			userHelper.getNotifyLoginResponse(userId);
		});

		assertEquals(ErrorCode.MEMBERSHIP_INFO_SETTING_FAIL, exception.getErrorCode());
	}

	@Test
	@Description("로그인 요청 중 예외 발생시 MEMBERSHIP_FEIGN_REQUEST_FAIL 예외 발생")
	void should_throw_MEMBERSHIP_FEIGN_REQUEST_FAIL_when_exception_occurs_during_login_request() {
		Long userId = 1L;
		when(membershipClient.notifyLogin(any()))
			.thenThrow(new RuntimeException("Feign client failure"));

		GlobalException exception = assertThrows(GlobalException.class, () -> {
			userHelper.getNotifyLoginResponse(userId);
		});

		assertEquals(ErrorCode.MEMBERSHIP_FEIGN_REQUEST_FAIL, exception.getErrorCode());
	}
}
