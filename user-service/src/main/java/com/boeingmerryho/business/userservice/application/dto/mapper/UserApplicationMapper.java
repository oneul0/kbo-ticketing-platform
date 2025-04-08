package com.boeingmerryho.business.userservice.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.boeingmerryho.business.userservice.application.dto.response.UserAdminFindResponseDto;
import com.boeingmerryho.business.userservice.application.dto.response.UserLoginResponseServiceDto;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserFindResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserLoginResponseDto;

@Mapper(componentModel = "spring")
public interface UserApplicationMapper {

	UserLoginResponseDto toUserLoginResponseDto(UserLoginResponseServiceDto responseServiceDto);

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

	// @Mapping(target = "id", source = "id")
	// @Mapping(target = "username", source = "username")
	// @Mapping(target = "password", source = "password")
	// @Mapping(target = "email", source = "email")
	// @Mapping(target = "role", source = "role")
	// @Mapping(target = "status", source = "status")
	// UserSearchResponseDto toUserSearchResponseDto(User user);

}
