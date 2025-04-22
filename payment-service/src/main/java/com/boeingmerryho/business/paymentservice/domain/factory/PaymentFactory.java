package com.boeingmerryho.business.paymentservice.domain.factory;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.domain.entity.AccountInfo;
import com.boeingmerryho.business.paymentservice.domain.entity.KakaoPayInfo;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;

@Component
public class PaymentFactory {

	public PaymentDetail createDetail(
		Payment payment,
		PaymentSession paymentSession,
		KakaoPayApproveResponse response,
		PaymentMethod method
	) {
		if (method == PaymentMethod.KAKAOPAY) {
			return PaymentDetail.builder()
				.kakaoPayInfo(
					KakaoPayInfo.builder()
						.cid(paymentSession.cid())
						.tid(paymentSession.tid())
						.build()
				)
				.payment(payment)
				.discountPrice(payment.getDiscountPrice() - response.amount().discount())
				.method(method)
				.discountAmount(payment.getTotalPrice() - payment.getDiscountPrice() + response.amount().discount())
				.build();
		}
		if (method == PaymentMethod.BANK_TRANSFER) {
			return PaymentDetail.builder()
				.payment(payment)
				.discountPrice(payment.getDiscountPrice())
				.method(method)
				.discountAmount(payment.getTotalPrice() - payment.getDiscountPrice())
				.accountInfo(createAccountInfo())
				.build();
		}
		return null;
	}

	private AccountInfo createAccountInfo() {
		String accountNumber = "110-333-482102";
		String accountBank = "SHINHAN";
		LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
		String accountHolder = "BOEING_KBO_" + "USERNAME";

		return AccountInfo.builder()
			.accountNumber(accountNumber)
			.accountBank(accountBank)
			.dueDate(dueDate)
			.accountHolder(accountHolder)
			.build();
	}
}
