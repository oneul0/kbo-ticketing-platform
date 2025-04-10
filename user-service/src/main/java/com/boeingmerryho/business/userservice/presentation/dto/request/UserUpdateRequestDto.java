package com.boeingmerryho.business.userservice.presentation.dto.request;

import java.time.LocalDate;

public record UserUpdateRequestDto(String password, String username, String nickname, LocalDate birth) {

}
