package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.paymentservice.application.PaymentAdminService;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/v1/payments")
@RequiredArgsConstructor
public class PaymentAdminController {
	private final PaymentAdminService paymentAdminService;
	private final PaymentPresentationMapper mapper;

	@GetMapping("/details/{id}")
	public ResponseEntity<SuccessResponse<PaymentDetailResponseDto>> getPaymentDetail(@PathVariable Long id) {

		PaymentDetailResponseServiceDto responseServiceDto = paymentAdminService.getPaymentDetail(
			mapper.toPaymentDetailRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			mapper.toPaymentDetailResponseDto(responseServiceDto));

	}
}
