package com.boeingmerryho.business.userservice.application.dto.request.admin;

public record UserAdminEmailVerificationCheckRequestServiceDto(String email, String code) {
}
