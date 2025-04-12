package com.boeingmerryho.business.membershipservice.application.dto.response;

import java.time.Year;

public record MembershipCreateResponseServiceDto(
	Long id,
	Year season,
	String name,
	Double discount
) {
}
