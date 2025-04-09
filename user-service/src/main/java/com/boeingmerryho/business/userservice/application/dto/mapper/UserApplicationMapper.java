package com.boeingmerryho.business.userservice.application.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.boeingmerryho.business.userservice.application.dto.response.UserAdminFindResponseDto;
import com.boeingmerryho.business.userservice.application.dto.response.UserLoginResponseServiceDto;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserFindResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserLoginResponseDto;

@Mapper(componentModel = "spring")
public interface UserApplicationMapper {

	UserLoginResponseDto toUserLoginResponseDto(UserLoginResponseServiceDto responseServiceDto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	UserFindResponseDto toUserFindResponseDto(User user);

	@Mapping(target = "id", source = "id")
	UserAdminUpdateResponseDto toUserAdminUpdateResponseDto(Long id);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "role", source = "role")
	@Mapping(target = "newRole", source = "newRole")
	UserAdminUpdateRoleResponseDto toUserAdminUpdateRoleResponseDto(
		Long id, UserRoleType role, UserRoleType newRole);

	UserAdminFindResponseDto toUserAdminFindResponseDto(User user);

	UserAdminSearchResponseDto toUserAdminSearchResponseDto(User user);

	UserCheckEmailResponseDto toUserCheckEmailResponseDto(Boolean idEmailDuplicated);

	UserAdminCheckEmailResponseDto toUserAdminCheckEmailResponseDto(Boolean isEmailDuplicated);

}
