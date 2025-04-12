package com.boeingmerryho.business.membershipservice.application.dto.response;

import java.time.Year;

public record MembershipDetailAdminResponseServiceDto(
	Long id,
	Year season,
	String name,
	Double discount
) {
}
