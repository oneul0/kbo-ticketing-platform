package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaoPayCancelRequest {
	String cid;
	String tid;
	@JsonProperty("cancel_amount")
	Integer cancelAmount;
	@JsonProperty("cancel_tax_free_amount")
	Integer cancelTaxFreeAmount;
}
