package com.boeingmerryho.business.paymentservice.infrastructure.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
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
import com.boeingmerryho.business.paymentservice.application.factory.PaymentStrategyFactory;
import com.boeingmerryho.business.paymentservice.application.service.PaymentAdminService;
import com.boeingmerryho.business.paymentservice.application.strategy.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.helper.PaymentReader;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;
import com.boeingmerryho.business.paymentservice.presentation.code.PaymentErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentAdminServiceImpl implements PaymentAdminService {

	private final PaymentReader paymentReader;
	private final PaymentStrategyFactory strategyFactory;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentReader.getPayment(requestServiceDto.id());
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentReader.getPayment(requestServiceDto.userId());
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentDetailAdminResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto
	) {
		PaymentDetail paymentDetail = paymentReader.getDetail(requestServiceDto.id());
		return paymentApplicationMapper.toPaymentDetailAdminResponseServiceDto(paymentDetail);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentDetailAdminResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto
	) {
		PaymentDetailSearchContext searchContext = createSearchContext(requestServiceDto);
		Page<PaymentDetail> paymentDetails = paymentReader.getPaymentDetails(searchContext);
		return paymentDetails.map(paymentApplicationMapper::toPaymentDetailAdminResponseServiceDto);
	}

	@Override
	@Transactional
	public PaymentRefundResponseServiceDto refundPayment(
		PaymentRefundRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentReader.getPayment(requestServiceDto.id());
		assertRefundablePayment(payment);

		PaymentDetail paymentDetail = paymentReader.getDetailByPaymentId(payment.getId());

		PaymentStrategy strategy = strategyFactory.getStrategy(paymentDetail.getMethod());
		return strategy.refund(paymentDetail);
	}

	@Override
	@Transactional
	public PaymentApproveResponseServiceDto approvePayment(
		PaymentApproveAdminRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentReader.getPayment(requestServiceDto.paymentId());
		PaymentStrategy strategy = strategyFactory.getStrategy(PaymentMethod.BANK_TRANSFER);
		return strategy.approve(payment, requestServiceDto);
	}

	private void assertCancellablePayment(
		Payment payment
	) {
		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
	}

	private void assertRefundablePayment(
		Payment payment
	) {
		if (payment.validateStatus(PaymentStatus.REFUNDED)) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
		}
		if (!payment.validateStatus(PaymentStatus.REFUND_REQUESTED)) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_REFUND_UNAVAILABLE);
		}
	}

	private PaymentDetailSearchContext createSearchContext(
		PaymentDetailSearchRequestServiceDto requestServiceDto
	) {
		return PaymentDetailSearchContext.builder()
			.id(requestServiceDto.id())
			.paymentId(requestServiceDto.paymentId())
			.customPageable(requestServiceDto.customPageable())
			.isDeleted(requestServiceDto.isDeleted())
			.build();
	}
}
