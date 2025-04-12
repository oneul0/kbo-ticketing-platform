package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoPaymentSession {
	private String tid;
	private String cid;
	private String partnerOrderId;
	private String partnerUserId;
	private List<String> tickets;
	private Integer totalAmount;
	private Integer quantity;
	private String itemName;
	private String createdAt;
}
