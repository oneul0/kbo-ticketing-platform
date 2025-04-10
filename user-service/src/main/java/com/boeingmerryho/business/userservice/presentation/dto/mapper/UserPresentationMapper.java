package com.boeingmerryho.business.userservice.presentation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.userservice.application.dto.request.UserAdminCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminDeleteRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminDeleteRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserCreateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserAdminLoginRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserAdminRegisterRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserAdminSearchRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserAdminUpdateRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserAdminUpdateRoleRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserCreateRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserLoginRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserRegisterRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserUpdateRequestDto;

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
	UserLogoutRequestServiceDto toUserLogoutRequestServiceDto(Long userId);

	UserAdminLoginRequestServiceDto toUserAdminLoginRequestServiceDto(UserAdminLoginRequestDto requestDto);

	@Mapping(target = "id", source = "id")
	UserUpdateRequestServiceDto toUserUpdateRequestServiceDto(UserUpdateRequestDto requestDto, Long id);

	@Mapping(target = "id", source = "id")
	UserWithdrawRequestServiceDto toUserWithdrawRequestServiceDto(Long id);

	@Mapping(target = "id", source = "id")
	UserAdminWithdrawRequestServiceDto toUserAdminWithdrawRequestServiceDto(Long id);

	UserAdminCheckEmailRequestServiceDto toUserAdminCheckEmailRequestServiceDto(String email);

	UserAdminRefreshTokenRequestServiceDto toUserAdminRefreshTokenRequestServiceDto(String refreshToken);

	UserCheckEmailRequestServiceDto toUserCheckEmailRequestServiceDto(String email);

	UserRefreshTokenRequestServiceDto toUserRefreshTokenRequestServiceDto(String refreshToken);
}
