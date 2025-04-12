package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardInfo {

	@JsonProperty("kakaopay_purchase_corp")
	private String kakaoPayPurchaseCorp;

	@JsonProperty("kakaopay_purchase_corp_code")
	private String kakaoPayPurchaseCorpCode;

	@JsonProperty("kakaopay_issuer_corp")
	private String kakaoPayIssuerCorp;

	@JsonProperty("kakaopay_issuer_corp_code")
	private String kakaoPayIssuerCorpCode;

	private String bin;

	@JsonProperty("card_type")
	private String cardType;

	@JsonProperty("install_month")
	private String installMonth;

	@JsonProperty("approved_id")
	private String approvedId;

	@JsonProperty("card_mid")
	private String cardMid;

	@JsonProperty("interest_free_install")
	private String interestFreeInstall;

	@JsonProperty("installment_type")
	private String installmentType;

	@JsonProperty("card_item_code")
	private String cardItemCode;
}
