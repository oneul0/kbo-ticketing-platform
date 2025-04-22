package com.boeingmerryho.business.paymentservice.application;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveAdminRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentRefundRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;

public interface PaymentAdminService {
	@Transactional
	PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto);

	@Transactional
	PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto);

	@Transactional(readOnly = true)
	PaymentDetailAdminResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto);

	@Transactional(readOnly = true)
	Page<PaymentDetailAdminResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto);

	@Transactional
	PaymentRefundResponseServiceDto refundPayment(
		PaymentRefundRequestServiceDto requestServiceDto);

	@Transactional
	PaymentApproveResponseServiceDto approvePayment(
		PaymentApproveAdminRequestServiceDto paymentApproveAdminRequestServiceDto);
}
