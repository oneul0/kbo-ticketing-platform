package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Amount {
	private Integer total;
	@JsonProperty("tax_free")
	private Integer taxFree;
	private Integer vat;
	private Integer point;
	private Integer discount;
	@JsonProperty("green_deposit")
	private Integer greenDeposit;
}