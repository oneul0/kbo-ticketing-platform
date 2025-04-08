package com.boeingmerryho.business.paymentservice.application;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentAdminService {
	private final PaymentRepository paymentRepository;
	private final PaymentApplicationMapper mapper;

	public PaymentDetailResponseServiceDto getPaymentDetail(PaymentDetailRequestServiceDto requestServiceDto) {
		PaymentDetail paymentDetail = paymentRepository.findPaymentDetailById(requestServiceDto.id())
			.orElseThrow(() -> new EntityNotFoundException("Payment not found"));

		return mapper.toPaymentDetailResponseDto(paymentDetail);
	}
}
