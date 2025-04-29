package com.boeingmerryho.business.paymentservice.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.paymentservice.application.service.PaymentFeignService;
import com.boeingmerryho.business.paymentservice.presentation.dto.PaymentPresentationMapper;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentCreationRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentCreationResponseDto;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentFeignController {

	private final PaymentFeignService paymentFeignService;
	private final PaymentPresentationMapper paymentPresentationMapper;

	@PostMapping("/payment-service/payments/create")
	@Timed(value = "payment_create_time", description = "결제 정보 생성 시간")
	@Counted(value = "payment_create_count", description = "결제 생성 요청 횟수")
	public PaymentCreationResponseDto createPayment(
		@RequestBody PaymentCreationRequestDto requestDto
	) {
		return paymentPresentationMapper.toPaymentCreationResponseDto(paymentFeignService.createPayment(
			paymentPresentationMapper.toPaymentCreationRequestServiceDto(
				requestDto
			))
		);
	}
}
