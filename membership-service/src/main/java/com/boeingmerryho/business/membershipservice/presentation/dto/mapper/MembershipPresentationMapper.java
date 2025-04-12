package com.boeingmerryho.business.membershipservice.presentation.dto.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipUpdateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipDetailResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUpdateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipCreateRequestDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipSearchAdminRequestServiceDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipUpdateRequestDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipCreateResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipDetailAdminResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipDetailResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipSearchAdminResponseDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipUpdateResponseDto;

import jakarta.validation.Valid;

@Primary
@Mapper(componentModel = "spring")
public interface MembershipPresentationMapper {

	MembershipCreateResponseDto toMembershipCreateResponseDto(MembershipCreateResponseServiceDto responseServiceDto);

	@BeanMapping(ignoreByDefault = true)
	MembershipCreateRequestServiceDto toMembershipCreateRequestServiceDto(@Valid MembershipCreateRequestDto requestDto);

	MembershipDetailAdminResponseDto toMembershipDetailAdminResponseDto(
		MembershipDetailAdminResponseServiceDto responseServiceDto);

	List<MembershipDetailResponseDto> toMembershipDetailResponseDto(
		List<MembershipDetailResponseServiceDto> responseServiceDto);

	MembershipSearchAdminRequestServiceDto toMembershipSearchAdminRequestServiceDto(
		Pageable customPageable,
		String name,
		Integer season,
		Double minDiscount,
		Double maxDiscount);

	MembershipSearchAdminResponseDto toMembershipSearchAdminResponseDto(
		MembershipSearchAdminResponseServiceDto membershipSearchAdminResponseServiceDto);

	MembershipUpdateResponseDto toMembershipUpdateResponseDto(MembershipUpdateResponseServiceDto responseServiceDto);

	MembershipUpdateRequestServiceDto toMembershipUpdateRequestServiceDto(MembershipUpdateRequestDto requestDto);
}
