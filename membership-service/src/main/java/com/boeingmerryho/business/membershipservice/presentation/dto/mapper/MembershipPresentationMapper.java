package com.boeingmerryho.business.membershipservice.presentation.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

import com.boeingmerryho.business.membershipservice.application.dto.request.MembershipCreateRequestServiceDto;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipCreateResponseServiceDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.request.MembershipCreateRequestDto;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipCreateResponseDto;

import jakarta.validation.Valid;

@Primary
@Mapper(componentModel = "spring")
public interface MembershipPresentationMapper {

	MembershipCreateResponseDto toMembershipCreateResponseDto(MembershipCreateResponseServiceDto responseServiceDto);

	@BeanMapping(ignoreByDefault = true)
	MembershipCreateRequestServiceDto toMembershipCreateRequestServiceDto(@Valid MembershipCreateRequestDto requestDto);
}
