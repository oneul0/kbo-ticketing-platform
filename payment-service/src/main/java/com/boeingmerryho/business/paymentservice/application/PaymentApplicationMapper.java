package com.boeingmerryho.business.paymentservice.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;

@Mapper(componentModel = "spring")
public interface PaymentApplicationMapper {

	@Mapping(target = "userId", source = "paymentDetail.payment.userId")
	@Mapping(target = "paymentId", source = "paymentDetail.payment.id")
	@Mapping(target = "price", source = "paymentDetail.payment.totalPrice")
	PaymentDetailResponseServiceDto toPaymentDetailResponseServiceDto(PaymentDetail paymentDetail);

	@Mapping(target = "userId", source = "paymentDetail.payment.userId")
	@Mapping(target = "paymentId", source = "paymentDetail.payment.id")
	@Mapping(target = "price", source = "paymentDetail.payment.totalPrice")
	PaymentDetailAdminResponseServiceDto toPaymentDetailAdminResponseServiceDto(PaymentDetail paymentDetail);

	PaymentTicketCancelResponseServiceDto toPaymentTicketCancelResponseServiceDto(Long id);

	PaymentMembershipCancelResponseServiceDto toPaymentMembershipCancelResponseServiceDto(Long id);
}
