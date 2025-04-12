package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPayCancelResponse {

	private String aid;
	private String tid;
	private String cid;
	private String status;
	@JsonProperty("partner_order_id")
	private String partnerOrderId;
	@JsonProperty("partner_user_id")
	private String partnerUserId;
	@JsonProperty("payment_method_type")
	private String paymentMethodType;
	private Amount amount;
	@JsonProperty("approved_cancel_amount")
	private ApprovedCancelAmount approvedCancelAmount;
	@JsonProperty("canceled_amount")
	private CanceledAmount canceledAmount;
	@JsonProperty("cancel_available_amount")
	private CancelAvailableAmount cancelAvailableAmount;
	@JsonProperty("item_name")
	private String itemName;
	@JsonProperty("item_code")
	private String itemCode;
	private Integer quantity;
	@JsonProperty("created_at")
	private LocalDateTime createdAt;
	@JsonProperty("approved_at")
	private LocalDateTime approvedAt;
	@JsonProperty("canceled_at")
	private LocalDateTime canceledAt;
	private String payload;

}
