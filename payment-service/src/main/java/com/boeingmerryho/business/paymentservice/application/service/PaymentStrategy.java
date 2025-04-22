package com.boeingmerryho.business.paymentservice.application.service;

import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveAdminRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;
import com.boeingmerryho.business.paymentservice.presentation.code.PaymentErrorCode;

public interface PaymentStrategy {
	PaymentMethod getSupportedMethod();

	@Transactional
	PaymentReadyResponseServiceDto pay(
		Payment payment,
		PaymentReadyRequestServiceDto requestDto
	);

	@Transactional
	default PaymentApproveResponseServiceDto approve(
		PaymentSession paymentSession,
		Payment payment,
		PaymentApproveRequestServiceDto requestDto) {
		throw new PaymentException(PaymentErrorCode.PAYMENT_UNSUPPORTED);
	}

	@Transactional
	default PaymentApproveResponseServiceDto approve(
		Payment payment,
		PaymentApproveAdminRequestServiceDto requestDto) {
		throw new PaymentException(PaymentErrorCode.PAYMENT_UNSUPPORTED);
	}

	@Transactional
	PaymentRefundResponseServiceDto refund(
		PaymentDetail paymentDetail
	);

}
