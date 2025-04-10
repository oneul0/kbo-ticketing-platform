package com.boeingmerryho.business.userservice.presentation.dto.request.other;

public record UserEmailVerificationCheckRequestDto(String email, String code) {
}
