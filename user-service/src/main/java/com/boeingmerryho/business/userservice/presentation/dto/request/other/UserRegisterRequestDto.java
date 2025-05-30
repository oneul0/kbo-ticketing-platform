package com.boeingmerryho.business.userservice.presentation.dto.request.other;

import java.time.LocalDate;

public record UserRegisterRequestDto(String email, String password, String username, String nickname,
									 LocalDate birth, String adminKey) {

}
