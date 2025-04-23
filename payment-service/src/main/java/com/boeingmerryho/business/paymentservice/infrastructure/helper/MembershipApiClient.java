package com.boeingmerryho.business.paymentservice.infrastructure.helper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.membership.UserDiscountRequest;
import com.boeingmerryho.business.paymentservice.infrastructure.feignclient.MembershipFeignClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipApiClient {
	private final MembershipFeignClient membershipFeignClient;

	public Optional<Double> getDiscount(Long userId) {
		return Optional.ofNullable(
			membershipFeignClient.getDiscount(new UserDiscountRequest(userId))
		);
	}
}
