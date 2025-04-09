package com.boeingmerryho.business.storeservice.presentation.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreCreateRequestDto(

	@NotNull(message = "stadiumId는 필수입니다.")
	Long stadiumId,

	@NotBlank(message = "매장 이름은 필수입니다.")
	String name,

	@NotNull(message = "openAt은 필수입니다.")
	LocalDateTime openAt,

	@NotNull(message = "closedAt은 필수입니다.")
	LocalDateTime closedAt,

	@NotNull(message = "isClosed는 필수입니다.")
	Boolean isClosed

) {
}
