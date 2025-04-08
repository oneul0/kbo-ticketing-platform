package com.boeingmerryho.business.userservice.application.dto.request;

import java.time.LocalDate;

public record UserUpdateRequestServiceDto(Long id, String password, String username, String nickname, LocalDate birth) {

}
