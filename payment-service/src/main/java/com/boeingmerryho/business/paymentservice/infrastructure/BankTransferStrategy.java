package com.boeingmerryho.business.paymentservice.infrastructure;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.AccountInfo;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BankTransferStrategy implements PaymentStrategy {

	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	public PaymentReadyResponseServiceDto pay(
		Payment payment,
		PaymentReadyRequestServiceDto requestDto) {

		String accountNumber = "110-333-482102";
		String accountBank = "SHINHAN";
		LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
		String accountHolder = "BOEING_KBO_" + "USERNAME";

		paymentDetailRepository.save(PaymentDetail.builder()
			.payment(payment)
			.discountPrice(payment.getDiscountPrice())
			.method(getSupportedMethod())
			.discountAmount(payment.getTotalPrice() - payment.getDiscountPrice())
			.accountInfo(
				AccountInfo.builder()
					.accountNumber(accountNumber)
					.accountBank(accountBank)
					.dueDate(dueDate)
					.accountHolder(accountHolder)
					.build()
			)
			.build()
		);

		return paymentApplicationMapper.toPaymentReadyResponseServiceDto(
			payment.getId(),
			accountNumber,
			accountBank,
			dueDate,
			accountHolder
		);
	}

	@Override
	public PaymentMethod getSupportedMethod() {
		return PaymentMethod.BANK_TRANSFER;
	}
}
