package com.boeingmerryho.business.userservice.presentation.dto.request;

import java.time.LocalDate;

public record UserAdminUpdateRequestDto(String password, String username, String nickname, LocalDate birth) {

}
