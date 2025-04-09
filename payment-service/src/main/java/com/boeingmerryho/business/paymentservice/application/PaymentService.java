package com.boeingmerryho.business.paymentservice.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.exception.PaymentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final PaymentApplicationMapperImpl mapper;

	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return mapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	private static void assertCancellablePayment(Payment payment) {

		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
	}

	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return mapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Transactional(readOnly = true)
	public PaymentDetailResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto) {
		PaymentDetail paymentDetail = paymentRepository.findPaymentDetailByIdAndIsDeleted(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
		return mapper.toPaymentDetailResponseServiceDto(paymentDetail);
	}

	@Transactional(readOnly = true)
	public Page<PaymentDetailResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		Page<PaymentDetail> paymentDetails = paymentRepository.searchPaymentDetail(
			createSearchContext(
				requestServiceDto.id(),
				requestServiceDto.paymentId(),
				requestServiceDto.customPageable(),
				requestServiceDto.isDeleted()
			)
		);
		return paymentDetails.map(mapper::toPaymentDetailResponseServiceDto);
	}

	private PaymentDetailSearchContext createSearchContext(
		Long id,
		Long paymentId,
		Pageable customPageable,
		Boolean isDeleted
	) {
		return PaymentDetailSearchContext.builder()
			.id(id)
			.paymentId(paymentId)
			.customPageable(customPageable)
			.isDeleted(isDeleted)
			.build();
	}
}
