package com.boeingmerryho.business.userservice.application.dto.request.admin;

import java.time.LocalDate;

public record UserAdminUpdateRequestServiceDto(Long id, String password, String username, String nickname,
											   LocalDate birth) {

}
