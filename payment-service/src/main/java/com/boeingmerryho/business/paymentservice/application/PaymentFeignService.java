package com.boeingmerryho.business.paymentservice.application;

import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentCreationRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentCreationResponseServiceDto;

public interface PaymentFeignService {

	@Transactional
	PaymentCreationResponseServiceDto createPayment(PaymentCreationRequestServiceDto requestServiceDto);
}
