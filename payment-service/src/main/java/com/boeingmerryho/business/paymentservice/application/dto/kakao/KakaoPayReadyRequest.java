package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class KakaoPayReadyRequest {
	@JsonProperty("cid")
	private String cid;

	@JsonProperty("partner_order_id")
	private String partnerOrderId;

	@JsonProperty("partner_user_id")
	private String partnerUserId;

	@JsonProperty("item_name")
	private String itemName;

	@JsonProperty("quantity")
	private int quantity;

	@JsonProperty("total_amount")
	private int totalAmount;

	@JsonProperty("vat_amount")
	private int vatAmount;

	@JsonProperty("tax_free_amount")
	private int taxFreeAmount;

	@JsonProperty("approval_url")
	private String approvalUrl;

	@JsonProperty("fail_url")
	private String failUrl;

	@JsonProperty("cancel_url")
	private String cancelUrl;
}