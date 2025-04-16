package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.paymentservice.application.PaymentFeignService;
import com.boeingmerryho.business.paymentservice.presentation.dto.PaymentPresentationMapper;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentCreationRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentCreationResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentFeignController {

	private final PaymentFeignService paymentFeignService;
	private final PaymentPresentationMapper paymentPresentationMapper;

	@PostMapping("/payments/create")
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
