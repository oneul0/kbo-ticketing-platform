package com.boeingmerryho.business.membershipservice.application.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUserCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUpdateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;

@Primary
@Mapper(componentModel = "spring")
public interface MembershipApplicationMapper {

	@BeanMapping(ignoreByDefault = true)
	MembershipUserCreateResponseServiceDto toMembershipUserCreateResponseServiceDto(
		MembershipUserCreateRequestServiceDto requestDto,
		Long paymentId);

	MembershipCreateResponseServiceDto toMembershipCreateResponseServiceDto(Membership saved);

	MembershipUpdateResponseServiceDto toMembershipUpdateResponseServiceDto(Membership updated);

	MembershipDetailResponseServiceDto toMembershipDetailResponseServiceDto(Membership membership);

	MembershipSearchAdminResponseServiceDto toMembershipSearchAdminResponseServiceDto(Membership membership);

	MembershipDetailAdminResponseServiceDto toMembershipDetailAdminResponseServiceDto(Membership membershipDetail);
}
