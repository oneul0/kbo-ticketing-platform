package com.boeingmerryho.business.membershipservice.presentation.dto.response;

import java.time.Year;

public record MembershipCreateResponseDto(
	Long id,
	Year season,
	String name,
	Double discount
) {
}
