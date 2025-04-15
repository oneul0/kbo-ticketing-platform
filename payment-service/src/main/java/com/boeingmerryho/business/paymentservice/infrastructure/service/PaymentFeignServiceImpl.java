package com.boeingmerryho.business.paymentservice.infrastructure.service;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.paymentservice.application.PaymentFeignService;
import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentCreationRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentCreationResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.PaySessionHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentFeignServiceImpl implements PaymentFeignService {

	private final PaySessionHelper paySessionHelper;
	private final PaymentRepository paymentRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	public PaymentCreationResponseServiceDto createPayment(PaymentCreationRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.save(
			Payment.builder()
				.userId(requestServiceDto.userId())
				.totalPrice(requestServiceDto.price() * requestServiceDto.quantity())
				.type(PaymentType.from(requestServiceDto.paymentType()))
				.build()
		);
		paySessionHelper.savePaymentPrice(payment.getId().toString(), requestServiceDto.price());
		paySessionHelper.savePaymentExpiredTime(payment.getId().toString(), requestServiceDto.expiredTime());
		return paymentApplicationMapper.toPaymentCreationResponseServiceDto(payment);
	}
}
