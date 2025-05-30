package com.boeingmerryho.business.paymentservice.presentation.dto;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveAdminRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentCreationRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipRefundRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentRefundRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentCreationResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentApproveRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentCreationRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentReadyRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentApproveResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentCreationResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailAdminResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipRefundResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentReadyResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentRefundResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketCancelResponseDto;

@Mapper(componentModel = "spring")
public interface PaymentPresentationMapper {

	PaymentTicketCancelRequestServiceDto toPaymentTicketCancelRequestServiceDto(
		Long userId,
		Long id
	);

	PaymentTicketCancelResponseDto toPaymentTicketCancelResponseDto(
		PaymentTicketCancelResponseServiceDto responseServiceDto);

	PaymentDetailRequestServiceDto toPaymentDetailRequestServiceDto(
		Long userId,
		Long id
	);

	PaymentDetailResponseDto toPaymentDetailResponseDto(
		PaymentDetailResponseServiceDto responseServiceDto);

	PaymentDetailSearchRequestServiceDto toPaymentDetailSearchRequestServiceDto(
		Pageable customPageable,
		Long id,
		Long userId,
		Long paymentId,
		Boolean isDeleted
	);

	PaymentDetailAdminResponseDto toPaymentDetailAdminResponseDto(
		PaymentDetailAdminResponseServiceDto responseServiceDto);

	PaymentMembershipCancelRequestServiceDto toPaymentMembershipCancelRequestServiceDto(
		Long userId,
		Long id
	);

	PaymentMembershipCancelResponseDto toPaymentMembershipCancelResponseDto(
		PaymentMembershipCancelResponseServiceDto responseServiceDto);

	PaymentReadyRequestServiceDto toPaymentReadyRequestServiceDto(
		Long userId,
		PaymentReadyRequestDto requestDto
	);

	PaymentReadyResponseDto toPaymentReadyResponseDto(
		PaymentReadyResponseServiceDto responseServiceDto);

	PaymentApproveAdminRequestServiceDto toPaymentApproveAdminRequestServiceDto(
		Long userId,
		PaymentApproveRequestDto requestDto
	);

	PaymentApproveRequestServiceDto toPaymentApproveRequestServiceDto(
		Long userId,
		String pgToken,
		PaymentApproveRequestDto requestDto
	);

	PaymentApproveResponseDto toPaymentApproveResponseDto(
		PaymentApproveResponseServiceDto responseServiceDto);

	PaymentMembershipRefundResponseDto toPaymentMembershipRefundResponseDto(
		PaymentRefundResponseServiceDto responseServiceDto);

	PaymentRefundResponseDto toPaymentRefundResponseDto(
		PaymentRefundResponseServiceDto responseServiceDto);

	PaymentRefundRequestServiceDto toPaymentRefundRequestServiceDto(
		Long userId,
		Long id
	);

	PaymentMembershipRefundRequestServiceDto toPaymentMembershipRefundRequestServiceDto(
		Long userId,
		Long id
	);

	PaymentCreationRequestServiceDto toPaymentCreationRequestServiceDto(
		PaymentCreationRequestDto requestDto);

	PaymentCreationResponseDto toPaymentCreationResponseDto(
		PaymentCreationResponseServiceDto responseServiceDto);
}
