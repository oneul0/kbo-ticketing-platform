package com.boeingmerryho.business.userservice.application.dto.request.other;

import java.time.LocalDate;

public record UserCreateRequestServiceDto(String email, String password, String username, String nickname,
										  LocalDate birth) {

}
