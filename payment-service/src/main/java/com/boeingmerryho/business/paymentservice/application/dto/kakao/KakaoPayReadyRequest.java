package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record KakaoPayReadyRequest(
	@JsonProperty("cid") String cid,
	@JsonProperty("partner_order_id") String partnerOrderId,
	@JsonProperty("partner_user_id") String partnerUserId,
	@JsonProperty("item_name") String itemName,
	@JsonProperty("quantity") int quantity,
	@JsonProperty("total_amount") int totalAmount,
	@JsonProperty("vat_amount") int vatAmount,
	@JsonProperty("tax_free_amount") int taxFreeAmount,
	@JsonProperty("approval_url") String approvalUrl,
	@JsonProperty("fail_url") String failUrl,
	@JsonProperty("cancel_url") String cancelUrl
) {
}