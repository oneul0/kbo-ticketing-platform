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
		boolean exists = membershipRepository.existsBySeasonAndName(season, MembershipType.valueOf(name));
		if (exists) {
			throw new GlobalException(MembershipErrorCode.ALREADY_REGISTERED);
		}
	}

	public void validateHasUpdatableFields(MembershipUpdateRequestServiceDto requestDto) {
		if (requestDto.season() == null && requestDto.name() == null && requestDto.discount() == null) {
			throw new GlobalException(MembershipErrorCode.NO_UPDATE_FIELDS_PROVIDED);
		}
	}
}
