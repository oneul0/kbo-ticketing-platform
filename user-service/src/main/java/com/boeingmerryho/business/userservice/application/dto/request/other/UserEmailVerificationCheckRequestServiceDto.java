package com.boeingmerryho.business.userservice.application.dto.request.other;

public record UserEmailVerificationCheckRequestServiceDto(String email, String code) {
}
