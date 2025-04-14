package com.boeingmerryho.business.membershipservice.infrastructure;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUserCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.request.PaymentRequestDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.infrastructure.client.PaymentFeignClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentApiClient {

	private final PaymentFeignClient paymentFeignClient;

	private final static Integer QUANTITY = 1;
	private final static String PAYMENT_TYPE = "MEMBERSHIP";

	public Long getPaymentId(MembershipUserCreateRequestServiceDto requestDto, Membership membership) {
		PaymentRequestDto paymentInfo = new PaymentRequestDto(
			requestDto.userId(),
			membership.getPrice(),
			QUANTITY,
			PAYMENT_TYPE,
			LocalDateTime.now());
		return paymentFeignClient.createPayment(paymentInfo);
	}

}
