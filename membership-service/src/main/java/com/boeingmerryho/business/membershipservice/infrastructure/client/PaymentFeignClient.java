package com.boeingmerryho.business.membershipservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.boeingmerryho.business.membershipservice.application.dto.request.PaymentRequestDto;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient {
	@PostMapping("/payments/create")
	Long createPayment(@RequestBody PaymentRequestDto dto);
}
