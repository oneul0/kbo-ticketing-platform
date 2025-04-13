package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUpdateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;
import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipValidator {

	private final MembershipRepository membershipRepository;

	public void validateNotDuplicated(Integer season, String name) {
		try {
			MembershipType type = MembershipType.valueOf(name);
			boolean exists = membershipRepository.existsBySeasonAndName(season, type);
			if (exists) {
				throw new GlobalException(MembershipErrorCode.ALREADY_REGISTERED);
			}
		} catch (IllegalArgumentException ex) {
			throw new GlobalException(MembershipErrorCode.INVALID_MEMBERSHIP_TYPE);
		}
	}

	public void validateHasUpdatableFields(MembershipUpdateRequestServiceDto requestDto) {
		if (requestDto.season() == null && requestDto.discount() == null && requestDto.availableQuantity() == null
			&& requestDto.price() == null) {
			throw new GlobalException(MembershipErrorCode.NO_UPDATE_FIELDS_PROVIDED);
		}
	}
}
