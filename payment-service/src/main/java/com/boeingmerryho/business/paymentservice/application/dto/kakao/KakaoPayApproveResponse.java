package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoPayApproveResponse {

	private String aid;
	private String tid;
	private String cid;
	private String sid;

	@JsonProperty("partner_order_id")
	private String partnerOrderId;

	@JsonProperty("partner_user_id")
	private String partnerUserId;

	@JsonProperty("payment_method_type")
	private String paymentMethodType;

	private Amount amount;

	@JsonProperty("card_info")
	private CardInfo cardInfo;

	@JsonProperty("item_name")
	private String itemName;

	@JsonProperty("item_code")
	private String itemCode;

	private Integer quantity;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@JsonProperty("approved_at")
	private LocalDateTime approvedAt;

	private String payload;
}
