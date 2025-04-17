package com.boeingmerryho.business.paymentservice.infrastructure.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.PaymentAdminService;
import com.boeingmerryho.business.paymentservice.application.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.application.PaymentStrategyFactory;
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
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentAdminServiceImpl implements PaymentAdminService {

	private final PaymentRepository paymentRepository;
	private final PaymentStrategyFactory strategyFactory;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto
	) {
		Payment payment = getPayment(requestServiceDto.id());
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto
	) {
		Payment payment = getPayment(requestServiceDto.userId());
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentDetailAdminResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto
	) {
		PaymentDetail paymentDetail = getDetail(requestServiceDto.id());
		return paymentApplicationMapper.toPaymentDetailAdminResponseServiceDto(paymentDetail);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentDetailAdminResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto
	) {
		Page<PaymentDetail> paymentDetails = getPaymentDetails(requestServiceDto);
		return paymentDetails.map(paymentApplicationMapper::toPaymentDetailAdminResponseServiceDto);
	}

	@Override
	@Transactional
	public PaymentRefundResponseServiceDto refundPayment(
		PaymentRefundRequestServiceDto requestServiceDto
	) {
		Payment payment = getPayment(requestServiceDto.id());
		assertRefundablePayment(payment);

		PaymentDetail paymentDetail = getDetailByPaymentId(payment.getId());

		PaymentStrategy strategy = strategyFactory.getStrategy(paymentDetail.getMethod());
		return strategy.refund(paymentDetail);
	}

	@Override
	@Transactional
	public PaymentApproveResponseServiceDto approvePayment(
		PaymentApproveAdminRequestServiceDto requestServiceDto
	) {
		Payment payment = getPayment(requestServiceDto.paymentId());
		PaymentStrategy strategy = strategyFactory.getStrategy(PaymentMethod.BANK_TRANSFER);
		return strategy.approve(payment, requestServiceDto);
	}

	private Payment getPayment(Long paymentId) {
		return paymentRepository.findById(paymentId)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
	}

	private PaymentDetail getDetail(Long detailId) {
		return paymentDetailRepository.findById(detailId)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
	}

	private PaymentDetail getDetailByPaymentId(Long paymentId) {
		return paymentDetailRepository.findPaymentDetailByPaymentId(paymentId)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
	}

	private Page<PaymentDetail> getPaymentDetails(PaymentDetailSearchRequestServiceDto requestServiceDto) {
		return paymentRepository.searchPaymentDetail(
			createSearchContext(requestServiceDto)
		);
	}

	private void assertCancellablePayment(
		Payment payment
	) {
		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
	}

	private void assertRefundablePayment(
		Payment payment
	) {
		if (payment.validateStatus(PaymentStatus.REFUNDED)) {
			throw new PaymentException(ErrorCode.PAYMENT_ALREADY_REFUNDED);
		}
		if (!payment.validateStatus(PaymentStatus.REFUND_REQUESTED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_UNAVAILABLE);
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
