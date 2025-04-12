package com.boeingmerryho.business.membershipservice.application.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;

@Primary
@Mapper(componentModel = "spring")
public interface MembershipApplicationMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	@Mapping(target = "deletedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	Membership toEntity(MembershipCreateRequestServiceDto requestDto);

	@BeanMapping(ignoreByDefault = true)
	MembershipCreateResponseServiceDto toMembershipCreateResponseServiceDto(Membership saved);

	MembershipDetailAdminResponseServiceDto toMembershipDetailAdminResponseServiceDto(Membership membershipDetail);

	MembershipDetailResponseServiceDto toMembershipDetailResponseServiceDto(Membership membership);

	MembershipSearchAdminResponseServiceDto toMembershipSearchAdminResponseServiceDto(Membership membership);
}
