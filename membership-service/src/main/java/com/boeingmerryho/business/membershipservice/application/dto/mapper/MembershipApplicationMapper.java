package com.boeingmerryho.business.membershipservice.application.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUpdateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;

@Primary
@Mapper(componentModel = "spring")
public interface MembershipApplicationMapper {

	@BeanMapping(ignoreByDefault = true)
	MembershipCreateResponseServiceDto toMembershipCreateResponseServiceDto(Membership saved);

	MembershipDetailAdminResponseServiceDto toMembershipDetailAdminResponseServiceDto(Membership membershipDetail);

	MembershipDetailResponseServiceDto toMembershipDetailResponseServiceDto(Membership membership);

	MembershipSearchAdminResponseServiceDto toMembershipSearchAdminResponseServiceDto(Membership membership);

	MembershipUpdateResponseServiceDto toMembershipUpdateResponseServiceDto(Membership updated);
}
