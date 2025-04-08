package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.paymentservice.application.PaymentService;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;
import com.boeingmerryho.business.paymentservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;
	private final PaymentPresentationMapper mapper;

	@GetMapping("/details/{id}")
	public ResponseEntity<SuccessResponse<PaymentDetailResponseDto>> getPaymentDetail(@PathVariable Long id) {

		PaymentDetailResponseServiceDto responseServiceDto = paymentService.getPaymentDetail(
			mapper.toPaymentDetailRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			mapper.toPaymentDetailResponseDto(responseServiceDto));

	}

}
