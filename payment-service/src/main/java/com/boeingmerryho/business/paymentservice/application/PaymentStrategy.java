package com.boeingmerryho.business.paymentservice.application;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;

public interface PaymentStrategy {
	PaymentMethod getSupportedMethod();

	PaymentReadyResponseServiceDto pay(
		Payment payment,
		PaymentReadyRequestServiceDto requestDto);

	default PaymentApproveResponseServiceDto approve(
		PaymentSession paymentSession,
		Payment payment,
		PaymentApproveRequestServiceDto requestDto) {
		throw new PaymentException(ErrorCode.PAYMENT_UNSUPPORTED);
	}
}
