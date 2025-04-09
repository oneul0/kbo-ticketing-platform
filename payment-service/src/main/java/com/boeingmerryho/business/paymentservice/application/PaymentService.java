package com.boeingmerryho.business.paymentservice.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final PaymentApplicationMapper mapper;

	public PaymentDetailResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto) {
		PaymentDetail paymentDetail = paymentRepository.findPaymentDetailByIdAndIsDeleted(requestServiceDto.id())
			.orElseThrow(() -> new EntityNotFoundException("Payment not found"));

		return mapper.toPaymentDetailResponseServiceDto(paymentDetail);
	}

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
