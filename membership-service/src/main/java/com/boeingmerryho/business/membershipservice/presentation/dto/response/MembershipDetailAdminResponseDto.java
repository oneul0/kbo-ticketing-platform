package com.boeingmerryho.business.membershipservice.presentation.dto.response;

import java.time.Year;

public record MembershipDetailAdminResponseDto(
	Long id,
	Year season,
	String name,
	Double discount
) {
}
