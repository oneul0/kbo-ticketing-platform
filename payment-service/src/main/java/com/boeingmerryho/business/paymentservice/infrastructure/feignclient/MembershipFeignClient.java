package com.boeingmerryho.business.paymentservice.infrastructure.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.boeingmerryho.business.paymentservice.application.dto.membership.UserDiscountRequest;

@FeignClient(name = "membership-service")
public interface MembershipFeignClient {
	@PostMapping(
		value = "/membership-service/payment/discount",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	Double getDiscount(
		@RequestBody UserDiscountRequest request
	);
}
