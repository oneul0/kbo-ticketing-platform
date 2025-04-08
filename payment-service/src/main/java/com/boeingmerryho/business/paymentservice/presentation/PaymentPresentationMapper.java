package com.boeingmerryho.business.paymentservice.presentation;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailAdminResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;

@Mapper(componentModel = "spring")
public interface PaymentPresentationMapper {
	PaymentDetailRequestServiceDto toPaymentDetailRequestServiceDto(Long paymentId);

	PaymentDetailResponseDto toPaymentDetailResponseDto(PaymentDetailResponseServiceDto responseServiceDto);

	PaymentDetailSearchRequestServiceDto toPaymentDetailSearchRequestServiceDto(
		Pageable customPageable,
		Long id,
		Long paymentId,
		Boolean isDeleted
	);

	PaymentDetailAdminResponseDto toPaymentDetailAdminResponseDto(
		PaymentDetailAdminResponseServiceDto responseServiceDto);
}
