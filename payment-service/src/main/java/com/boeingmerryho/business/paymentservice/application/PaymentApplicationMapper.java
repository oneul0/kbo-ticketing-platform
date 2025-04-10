package com.boeingmerryho.business.paymentservice.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;

@Mapper(componentModel = "spring")
public interface PaymentApplicationMapper {

	@Mapping(target = "userId", source = "paymentDetail.payment.userId")
	@Mapping(target = "paymentId", source = "paymentDetail.payment.id")
	@Mapping(target = "price", source = "paymentDetail.payment.totalPrice")
	@Mapping(target = "discountType", source = "paymentDetail.payment.discountType")
	PaymentDetailAdminResponseServiceDto toPaymentDetailAdminResponseServiceDto(PaymentDetail paymentDetail);

	@Mapping(target = "userId", source = "paymentDetail.payment.userId")
	@Mapping(target = "paymentId", source = "paymentDetail.payment.id")
	@Mapping(target = "price", source = "paymentDetail.payment.totalPrice")
	@Mapping(target = "discountType", source = "paymentDetail.payment.discountType")
	PaymentDetailResponseServiceDto toPaymentDetailResponseServiceDto(PaymentDetail paymentDetail);

	PaymentTicketCancelResponseServiceDto toPaymentTicketCancelResponseServiceDto(Long id);

	PaymentMembershipCancelResponseServiceDto toPaymentMembershipCancelResponseServiceDto(Long id);

	PaymentReadyResponseServiceDto toPaymentReadyResponseServiceDto(KakaoPayReadyResponse response);

	PaymentApproveResponseServiceDto toPaymentApproveResponseServiceDto(KakaoPayApproveResponse response);

}
