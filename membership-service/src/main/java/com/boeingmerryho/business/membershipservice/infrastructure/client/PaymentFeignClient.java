package com.boeingmerryho.business.membershipservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.boeingmerryho.business.membershipservice.application.dto.request.PaymentRequestDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.PaymentCreationResponseDto;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient {
	@PostMapping("/payments/create")
	PaymentCreationResponseDto createPayment(@RequestBody PaymentRequestDto dto);
}
