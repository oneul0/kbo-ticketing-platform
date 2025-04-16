package com.boeingmerryho.business.membershipservice.application.service.user;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUserCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.infrastructure.PaymentApiClient;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipHelper;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipRedisHelper;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.PaymentCreationResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipReserveService {

	private final PaymentApiClient paymentApiClient;
	private final MembershipHelper membershipHelper;
	private final MembershipApplicationMapper mapper;
	private final MembershipRedisHelper reservationHelper;

	public MembershipUserCreateResponseServiceDto reserveMembership(
		MembershipUserCreateRequestServiceDto requestDto
	) {
		reservationHelper.reserve(requestDto.membershipId(), requestDto.userId(), Duration.ofMinutes(8));

		Membership membership = membershipHelper.readActiveMembership(requestDto.membershipId());
		PaymentCreationResponseDto responseDto = paymentApiClient.getPaymentId(requestDto, membership);

		return mapper.toMembershipUserCreateResponseServiceDto(requestDto, responseDto.paymentId());
	}
}
