package com.boeingmerryho.business.userservice.presentation.dto.request;

import java.time.LocalDate;

public record UserAdminRegisterRequestDto(String email, String password, String username, String nickname,
										  LocalDate birth, String adminKey) {

}
