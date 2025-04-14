package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.dto.query.MembershipSearchCondition;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUpdateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipQueryRepository;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipRepository;
import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipSearchAdminRequestServiceDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipAdminHelper {

	private final MembershipRepository membershipRepository;
	private final MembershipQueryRepository membershipQueryRepository;

	public Membership save(MembershipCreateRequestServiceDto requestDto) {
		Membership membership = Membership.create(requestDto);
		return membershipRepository.save(membership);
	}

	public Membership getAnyMembershipById(Long id) {
		return membershipRepository.findById(id)
			.orElseThrow(() -> new GlobalException(MembershipErrorCode.NOT_FOUND));
	}

	public Page<Membership> search(MembershipSearchAdminRequestServiceDto requestServiceDto) {
		MembershipSearchCondition condition = new MembershipSearchCondition(
			requestServiceDto.season(),
			requestServiceDto.name(),
			requestServiceDto.minDiscount(),
			requestServiceDto.maxDiscount(),
			requestServiceDto.minAvailableQuantity(),
			requestServiceDto.maxAvailableQuantity(),
			requestServiceDto.minPrice(),
			requestServiceDto.maxPrice(),
			requestServiceDto.isDeleted()
		);
		return membershipQueryRepository.search(condition, requestServiceDto.customPageable());
	}

	public Membership updateMembershipInfo(Long id, MembershipUpdateRequestServiceDto requestDto) {
		Membership membership = membershipRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new GlobalException(MembershipErrorCode.NOT_FOUND));

		membership.update(requestDto);

		return membership;
	}

	public void deleteStore(Long id) {
		Membership membership = membershipRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new GlobalException(MembershipErrorCode.NOT_FOUND));
		// TODO: 사용자 받아올 시 변경 필요
		membership.softDelete(1L);
	}
}
