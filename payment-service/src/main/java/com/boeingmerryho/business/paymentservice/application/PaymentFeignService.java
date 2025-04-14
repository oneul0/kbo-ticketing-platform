package com.boeingmerryho.business.paymentservice.application;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentCreationRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentCreationResponseServiceDto;

public interface PaymentFeignService {
	PaymentCreationResponseServiceDto createPayment(PaymentCreationRequestServiceDto requestServiceDto);
}
