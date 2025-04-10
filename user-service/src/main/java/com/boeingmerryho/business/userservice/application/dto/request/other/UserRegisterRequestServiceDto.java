package com.boeingmerryho.business.userservice.application.dto.request.other;

import java.time.LocalDate;

public record UserRegisterRequestServiceDto(String email, String password, String username, String nickname,
											LocalDate birth, String adminKey) {

}
