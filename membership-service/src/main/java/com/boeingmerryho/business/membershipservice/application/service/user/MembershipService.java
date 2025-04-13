package com.boeingmerryho.business.membershipservice.application.service.user;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.application.dto.mapper.MembershipApplicationMapper;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUserCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.request.PaymentRequestDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.infrastructure.client.PaymentFeignClient;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipHelper;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipRedisHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipService {

	private final MembershipHelper membershipHelper;
	private final MembershipApplicationMapper mapper;
	private final PaymentFeignClient paymentFeignClient;
	private final MembershipRedisHelper reservationHelper;

	@Transactional(readOnly = true)
	public List<MembershipDetailResponseServiceDto> getMembershipsByCurrentSeason() {
		List<Membership> memberships = membershipHelper.findAllBySeason();

		return memberships.stream()
			.map(mapper::toMembershipDetailResponseServiceDto)
			.toList();
	}

	public MembershipUserCreateResponseServiceDto reserveMembership(
		MembershipUserCreateRequestServiceDto requestDto
	) {
		reservationHelper.reserve(requestDto.membershipId(), requestDto.userId(), Duration.ofMinutes(10));
		PaymentRequestDto paymentInfo = new PaymentRequestDto(requestDto.userId(), requestDto.membershipId());
		Long paymentId = paymentFeignClient.createPayment(paymentInfo);
		return mapper.toMembershipUserCreateResponseServiceDto(requestDto, paymentId);
	}
}
