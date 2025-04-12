package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import java.time.Year;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.domain.type.MembershipType;
import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipValidator {

	private final MembershipRepository membershipRepository;

	public void validateNotDuplicated(Year season, String name) {
		boolean exists = membershipRepository.existsBySeasonAndName(season, MembershipType.valueOf(name));
		if (exists) {
			throw new GlobalException(MembershipErrorCode.ALREADY_REGISTERED);
		}
	}
}
