package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CanceledAmount(
	@JsonProperty("total") Integer total,
	@JsonProperty("tax_free") Integer taxFree,
	@JsonProperty("vat") Integer vat,
	@JsonProperty("point") Integer point,
	@JsonProperty("discount") Integer discount,
	@JsonProperty("green_deposit") Integer greenDeposit
) {
}
