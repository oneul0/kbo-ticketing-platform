package com.boeingmerryho.business.paymentservice.domain;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentReader {

	private final PaymentRepository paymentRepository;
	private final PaymentDetailRepository paymentDetailRepository;

	public Payment getPayment(
		Long paymentId
	) {
		return paymentRepository.findById(paymentId)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
	}

	public PaymentDetail getDetail(
		Long detailId
	) {
		return paymentDetailRepository.findById(detailId)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
	}

	public PaymentDetail getDetailByPaymentId(
		Long paymentId
	) {
		return paymentDetailRepository.findPaymentDetailByPaymentId(paymentId)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
	}

	public Page<PaymentDetail> getPaymentDetails(
		PaymentDetailSearchContext context
	) {
		return paymentRepository.searchPaymentDetail(context);
	}

}
