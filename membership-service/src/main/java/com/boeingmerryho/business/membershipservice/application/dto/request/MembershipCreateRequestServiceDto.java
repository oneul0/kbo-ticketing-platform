package com.boeingmerryho.business.membershipservice.application.dto.request;

import java.time.Year;

public record MembershipCreateRequestServiceDto(
	Year season,
	String name,
	Double discount
) {
}
