package com.boeingmerryho.business.paymentservice.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentAdminService {
	private final PaymentRepository paymentRepository;
	private final PaymentApplicationMapper mapper;

	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return mapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	private static void assertCancellablePayment(Payment payment) {
		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new RuntimeException("Payment not confirmed");
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new RuntimeException("Payment type not supported");
		}
	}

	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return mapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Transactional(readOnly = true)
	public PaymentDetailAdminResponseServiceDto getPaymentDetail(PaymentDetailRequestServiceDto requestServiceDto) {
		PaymentDetail paymentDetail = paymentRepository.findPaymentDetailById(requestServiceDto.id())
			.orElseThrow(() -> new EntityNotFoundException("Payment not found"));

		return mapper.toPaymentDetailAdminResponseServiceDto(paymentDetail);
	}

	@Transactional(readOnly = true)
	public Page<PaymentDetailAdminResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		Page<PaymentDetail> paymentDetails = paymentRepository.searchPaymentDetail(
			createSearchContext(
				requestServiceDto.id(),
				requestServiceDto.paymentId(),
				requestServiceDto.customPageable(),
				requestServiceDto.isDeleted()
			)
		);
		return paymentDetails.map(mapper::toPaymentDetailAdminResponseServiceDto);
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
