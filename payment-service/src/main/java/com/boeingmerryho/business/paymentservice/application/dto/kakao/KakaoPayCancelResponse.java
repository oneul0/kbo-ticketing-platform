package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoPayCancelResponse(
	@JsonProperty("aid") String aid,
	@JsonProperty("tid") String tid,
	@JsonProperty("cid") String cid,
	@JsonProperty("status") String status,
	@JsonProperty("partner_order_id") String partnerOrderId,
	@JsonProperty("partner_user_id") String partnerUserId,
	@JsonProperty("payment_method_type") String paymentMethodType,
	@JsonProperty("amount") Amount amount,
	@JsonProperty("approved_cancel_amount") ApprovedCancelAmount approvedCancelAmount,
	@JsonProperty("canceled_amount") CanceledAmount canceledAmount,
	@JsonProperty("cancel_available_amount") CancelAvailableAmount cancelAvailableAmount,
	@JsonProperty("item_name") String itemName,
	@JsonProperty("item_code") String itemCode,
	@JsonProperty("quantity") Integer quantity,
	@JsonProperty("created_at") LocalDateTime createdAt,
	@JsonProperty("approved_at") LocalDateTime approvedAt,
	@JsonProperty("canceled_at") LocalDateTime canceledAt,
	@JsonProperty("payload") String payload
) {
}
