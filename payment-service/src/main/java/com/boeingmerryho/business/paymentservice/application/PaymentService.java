package com.boeingmerryho.business.paymentservice.application;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;

public interface PaymentService {

	@Transactional
	PaymentReadyResponseServiceDto pay(PaymentReadyRequestServiceDto requestServiceDto);

	@Transactional
	PaymentApproveResponseServiceDto approvePayment(PaymentApproveRequestServiceDto requestServiceDto);

	@Transactional
	PaymentTicketCancelResponseServiceDto cancelTicketPayment(PaymentTicketCancelRequestServiceDto requestServiceDto);

	@Transactional
	PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto);

	@Transactional(readOnly = true)
	PaymentDetailResponseServiceDto getPaymentDetail(PaymentDetailRequestServiceDto requestServiceDto);

	@Transactional(readOnly = true)
	Page<PaymentDetailResponseServiceDto> searchPaymentDetail(PaymentDetailSearchRequestServiceDto requestServiceDto);
}
