package com.boeingmerryho.business.membershipservice.application.service.feign;

import com.boeingmerryho.business.membershipservice.application.dto.request.LoginSuccessRequest;

public interface MembershipFeignService {
	void handleLoginSuccess(LoginSuccessRequest request);

	Double getDiscountById(Long userId);
}
