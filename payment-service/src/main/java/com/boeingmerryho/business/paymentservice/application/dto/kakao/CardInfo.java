package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CardInfo(
	@JsonProperty("kakaopay_purchase_corp") String kakaoPayPurchaseCorp,
	@JsonProperty("kakaopay_purchase_corp_code") String kakaoPayPurchaseCorpCode,
	@JsonProperty("kakaopay_issuer_corp") String kakaoPayIssuerCorp,
	@JsonProperty("kakaopay_issuer_corp_code") String kakaoPayIssuerCorpCode,
	@JsonProperty("bin") String bin,
	@JsonProperty("card_type") String cardType,
	@JsonProperty("install_month") String installMonth,
	@JsonProperty("approved_id") String approvedId,
	@JsonProperty("card_mid") String cardMid,
	@JsonProperty("interest_free_install") String interestFreeInstall,
	@JsonProperty("installment_type") String installmentType,
	@JsonProperty("card_item_code") String cardItemCode
) {
}
