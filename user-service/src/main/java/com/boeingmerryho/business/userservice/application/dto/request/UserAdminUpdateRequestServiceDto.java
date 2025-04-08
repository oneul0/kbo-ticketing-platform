package com.boeingmerryho.business.userservice.application.dto.request;

import java.time.LocalDate;

public record UserAdminUpdateRequestServiceDto(String password, String username, String nickname, LocalDate birth) {

}
