package com.boeingmerryho.business.paymentservice.presentation;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailAdminResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketCancelResponseDto;

@Mapper(componentModel = "spring")
public interface PaymentPresentationMapper {

	PaymentTicketCancelRequestServiceDto toPaymentTicketCancelRequestServiceDto(Long id);

	PaymentTicketCancelResponseDto toPaymentTicketCancelResponseDto(
		PaymentTicketCancelResponseServiceDto responseServiceDto);

	PaymentDetailRequestServiceDto toPaymentDetailRequestServiceDto(Long id);

	PaymentDetailResponseDto toPaymentDetailResponseDto(PaymentDetailResponseServiceDto responseServiceDto);

	PaymentDetailSearchRequestServiceDto toPaymentDetailSearchRequestServiceDto(
		Pageable customPageable,
		Long id,
		Long userId,
		Long paymentId,
		Boolean isDeleted
	);

	PaymentDetailAdminResponseDto toPaymentDetailAdminResponseDto(
		PaymentDetailAdminResponseServiceDto responseServiceDto);

	PaymentMembershipCancelRequestServiceDto toPaymentMembershipCancelRequestServiceDto(Long id);

	PaymentMembershipCancelResponseDto toPaymentMembershipCancelResponseDto(
		PaymentMembershipCancelResponseServiceDto responseServiceDto);
}
