package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.paymentservice.application.PaymentService;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketCancelResponseDto;
import com.boeingmerryho.business.paymentservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;
	private final PaymentPresentationMapper mapper;

	@PutMapping("/{id}/cancel/tickets")
	public ResponseEntity<SuccessResponse<PaymentTicketCancelResponseDto>> cancelTicketPayment(@PathVariable Long id) {
		PaymentTicketCancelResponseServiceDto responseServiceDto = paymentService.cancelTicketPayment(
			mapper.toPaymentTicketCancelRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.REQUESTED_REFUND_TICKET,
			mapper.toPaymentTicketCancelResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/memberships")
	public ResponseEntity<SuccessResponse<PaymentMembershipCancelResponseDto>> cancelMembershipPayment(
		@PathVariable Long id) {
		PaymentMembershipCancelResponseServiceDto responseServiceDto = paymentService.cancelMembershipPayment(
			mapper.toPaymentMembershipCancelRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.REQUESTED_REFUND_MEMBERSHIP,
			mapper.toPaymentMembershipCancelResponseDto(responseServiceDto));
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<SuccessResponse<PaymentDetailResponseDto>> getPaymentDetail(@PathVariable Long id) {

		PaymentDetailResponseServiceDto responseServiceDto = paymentService.getPaymentDetail(
			mapper.toPaymentDetailRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			mapper.toPaymentDetailResponseDto(responseServiceDto));

	}

	@GetMapping("/details")
	public ResponseEntity<SuccessResponse<Page<PaymentDetailResponseDto>>> searchPaymentDetail(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "id", required = false) Long id,
		@RequestParam(value = "paymentId", required = false) Long paymentId
	) {

		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);

		Long userId = 1L;

		Page<PaymentDetailResponseServiceDto> responseServiceDto = paymentService.searchPaymentDetail(
			mapper.toPaymentDetailSearchRequestServiceDto(pageable, id, userId, paymentId,
				Boolean.FALSE));    // TODO userId
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			responseServiceDto.map(mapper::toPaymentDetailResponseDto));

	}

}
