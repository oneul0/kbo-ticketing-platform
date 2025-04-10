package com.boeingmerryho.business.userservice.presentation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminDeleteRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminDeleteRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminSearchRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminUpdateRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserCreateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminLoginRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminLogoutRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminRegisterRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminSearchRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminTokenRefreshRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminUpdateRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminUpdateRoleRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserCreateRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserLoginRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserLogoutRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserRegisterRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserTokenRefreshRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserUpdateRequestDto;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {

	UserRegisterRequestServiceDto toUserRegisterRequestServiceDto(UserRegisterRequestDto requestDto);

	UserLoginRequestServiceDto toUserLoginRequestServiceDto(UserLoginRequestDto requestDto);

	@Mapping(target = "id", source = "userId")
	UserFindRequestServiceDto toUserSearchRequestServiceDto(Long userId);

	UserAdminRegisterRequestServiceDto toUserAdminSignUpServiceDto(
		UserAdminRegisterRequestDto requestDto);

	UserCreateRequestServiceDto toUserCreateRequestServiceDto(
		UserCreateRequestDto requestDto);

	@Mapping(target = "id", source = "id")
	UserAdminFindRequestServiceDto toUserAdminFindRequestServiceDto(Long id);

	UserAdminSearchRequestServiceDto toUserAdminSearchRequestServiceDto(
		UserAdminSearchRequestDto requestDto, Pageable pageable);

	@Mapping(target = "id", source = "id")
	UserAdminUpdateRequestServiceDto toUserAdminUpdateRequestServiceDto(UserAdminUpdateRequestDto requestDto,
		Long id);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "newRole", expression = "java(requestDto.role())")
	UserAdminUpdateRoleRequestServiceDto toUserAdminUpdateRoleRequestServiceDto(
		UserAdminUpdateRoleRequestDto requestDto, Long id);

	@Mapping(target = "id", source = "id")
	UserAdminDeleteRoleRequestServiceDto toUserAdminDeleteRoleRequestServiceDto(Long id);

	@Mapping(target = "id", source = "id")
	UserAdminDeleteRequestServiceDto toUserAdminDeleteRequestServiceDto(Long id);

	@Mapping(target = "id", source = "userId")
	UserLogoutRequestServiceDto toUserLogoutRequestServiceDto(UserLogoutRequestDto requestDto, Long userId);

	UserAdminLoginRequestServiceDto toUserAdminLoginRequestServiceDto(UserAdminLoginRequestDto requestDto);

	@Mapping(target = "id", source = "id")
	UserUpdateRequestServiceDto toUserUpdateRequestServiceDto(UserUpdateRequestDto requestDto, Long id);

	@Mapping(target = "id", source = "id")
	UserWithdrawRequestServiceDto toUserWithdrawRequestServiceDto(Long id);

	@Mapping(target = "id", source = "id")
	UserAdminWithdrawRequestServiceDto toUserAdminWithdrawRequestServiceDto(Long id);

	UserAdminCheckEmailRequestServiceDto toUserAdminCheckEmailRequestServiceDto(String email);

	UserAdminRefreshTokenRequestServiceDto toUserAdminRefreshTokenRequestServiceDto(
		UserAdminTokenRefreshRequestDto refreshToken);

	UserCheckEmailRequestServiceDto toUserCheckEmailRequestServiceDto(String email);

	UserRefreshTokenRequestServiceDto toUserRefreshTokenRequestServiceDto(UserTokenRefreshRequestDto requestDto);

	UserLogoutRequestServiceDto toUserAdminLogoutRequestServiceDto(UserAdminLogoutRequestDto requestDto, Long userId);
}
