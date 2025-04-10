package com.boeingmerryho.business.userservice.presentation.dto.request.admin;

public record UserAdminEmailVerificationCheckRequestDto(String email, String code) {
}
