package com.boeingmerryho.business.userservice.application.dto.request.admin;

import java.time.LocalDate;

public record UserAdminRegisterRequestServiceDto(String email, String password, String username, String nickname,
												 LocalDate birth, String adminKey) {

}
