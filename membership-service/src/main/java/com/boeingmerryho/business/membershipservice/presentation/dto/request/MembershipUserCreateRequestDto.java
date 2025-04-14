package com.boeingmerryho.business.membershipservice.presentation.dto.request;

import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;

import jakarta.validation.constraints.NotNull;

public record MembershipUserCreateRequestDto(
	@NotNull(message = "멤버십은 필수입니다.")
	MembershipType membershipType,

	Long userId
) {
}
