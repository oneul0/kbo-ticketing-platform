package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.paymentservice.application.PaymentAdminService;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailAdminResponseDto;
import com.boeingmerryho.business.paymentservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/v1/payments")
@RequiredArgsConstructor
public class PaymentAdminController {
	private final PaymentAdminService paymentAdminService;
	private final PaymentPresentationMapper mapper;

	@GetMapping("/details/{id}")
	public ResponseEntity<SuccessResponse<PaymentDetailAdminResponseDto>> getPaymentDetail(@PathVariable Long id) {

		PaymentDetailAdminResponseServiceDto responseServiceDto = paymentAdminService.getPaymentDetail(
			mapper.toPaymentDetailRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			mapper.toPaymentDetailAdminResponseDto(responseServiceDto));

	}

	@GetMapping("/details")
	public ResponseEntity<SuccessResponse<Page<PaymentDetailAdminResponseDto>>> searchPaymentDetail(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "id", required = false) Long id,
		@RequestParam(value = "paymentId", required = false) Long paymentId,
		@RequestParam(value = "isDeleted", required = false) Boolean isDeleted
	) {

		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);

		Page<PaymentDetailAdminResponseServiceDto> responseServiceDto = paymentAdminService.searchPaymentDetail(
			mapper.toPaymentDetailSearchRequestServiceDto(pageable, id, paymentId, isDeleted));
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			responseServiceDto.map(mapper::toPaymentDetailAdminResponseDto));

	}
}
