package com.boeingmerryho.business.ticketservice.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.boeingmerryho.business.ticketservice.application.feign.dto.request.PaymentCreationRequestDto;
import com.boeingmerryho.business.ticketservice.application.feign.dto.response.PaymentCreationResponseDto;

@FeignClient(name = "payment-service")
public interface PaymentClient {

	@PostMapping("/payment-service/payments/create")
	PaymentCreationResponseDto createPayment(PaymentCreationRequestDto requestDto);
}
