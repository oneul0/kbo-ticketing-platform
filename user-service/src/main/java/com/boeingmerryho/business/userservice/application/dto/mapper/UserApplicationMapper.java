package com.boeingmerryho.business.userservice.application.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.boeingmerryho.business.userservice.application.dto.response.admin.UserAdminFindResponseDto;
import com.boeingmerryho.business.userservice.application.dto.response.admin.UserAdminLoginResponseServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.other.UserLoginResponseServiceDto;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminLoginResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminSearchResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateRoleResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminVerificationResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserFindResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserLoginResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserVerificationResponseDto;

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

	UserAdminVerificationResponseDto toUserAdminVerificationResponseDto(String email);

	UserVerificationResponseDto toUserVerificationResponseDto(String email);

	UserAdminLoginResponseDto toUserAdminLoginResponseDto(UserAdminLoginResponseServiceDto serviceDto);
}
