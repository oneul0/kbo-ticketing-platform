package com.boeingmerryho.business.paymentservice.presentation;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;

@Mapper(componentModel = "spring")
public interface PaymentPresentationMapper {
	PaymentDetailRequestServiceDto toPaymentDetailRequestServiceDto(Long id);
	PaymentDetailResponseDto toPaymentDetailResponseDto(PaymentDetailResponseServiceDto responseServiceDto);
}
