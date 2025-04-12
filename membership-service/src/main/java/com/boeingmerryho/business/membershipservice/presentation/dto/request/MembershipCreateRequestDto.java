package com.boeingmerryho.business.membershipservice.presentation.dto.request;

import java.time.Year;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MembershipCreateRequestDto(
	@NotNull(message = "seson은 필수입니다.")
	Year season,

	@NotBlank(message = "name은 필수입니다.")
	String name,

	@NotNull(message = "discount는 필수입니다.")
	Double discount
) {
}
