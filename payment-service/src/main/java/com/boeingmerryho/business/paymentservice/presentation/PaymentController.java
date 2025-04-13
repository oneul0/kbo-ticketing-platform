package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.paymentservice.application.PaymentService;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentReadyRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentApproveResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentReadyResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketCancelResponseDto;
import com.boeingmerryho.business.paymentservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
	private final PaymentService paymentService;
	private final PaymentPresentationMapper paymentPresentationMapper;

	@PostMapping("/pay")
	public ResponseEntity<SuccessResponse<PaymentReadyResponseDto>> pay(
		@RequestBody @Valid PaymentReadyRequestDto requestDto) {
		PaymentReadyResponseServiceDto responseServiceDto = paymentService.pay(
			paymentPresentationMapper.toPaymentReadyRequestServiceDto(1L, requestDto));    // TODO userId

		return SuccessResponse.of(PaymentSuccessCode.PAYMENT_READY_REQUESTED,
			paymentPresentationMapper.toPaymentReadyResponseDto(responseServiceDto));
	}

	@GetMapping("/approve")
	public ResponseEntity<SuccessResponse<PaymentApproveResponseDto>> approvePayment(
		@RequestParam("pg_token") String pgToken,
		@RequestParam("paymentId") Long paymentId
	) {
		PaymentApproveResponseServiceDto responseServiceDto = paymentService.approvePayment(
			paymentPresentationMapper.toPaymentApproveRequestServiceDto(1L, pgToken, paymentId));    // TODO userId

		return SuccessResponse.of(PaymentSuccessCode.PAYMENT_APPROVED,
			paymentPresentationMapper.toPaymentApproveResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/tickets")
	public ResponseEntity<SuccessResponse<PaymentTicketCancelResponseDto>> cancelTicketPayment(@PathVariable Long id) {
		PaymentTicketCancelResponseServiceDto responseServiceDto = paymentService.cancelTicketPayment(
			paymentPresentationMapper.toPaymentTicketCancelRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.TICKET_REFUND_REQUESTED,
			paymentPresentationMapper.toPaymentTicketCancelResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/memberships")
	public ResponseEntity<SuccessResponse<PaymentMembershipCancelResponseDto>> cancelMembershipPayment(
		@PathVariable Long id) {
		PaymentMembershipCancelResponseServiceDto responseServiceDto = paymentService.cancelMembershipPayment(
			paymentPresentationMapper.toPaymentMembershipCancelRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.MEMBERSHIP_REFUND_REQUESTED,
			paymentPresentationMapper.toPaymentMembershipCancelResponseDto(responseServiceDto));
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<SuccessResponse<PaymentDetailResponseDto>> getPaymentDetail(@PathVariable Long id) {

		PaymentDetailResponseServiceDto responseServiceDto = paymentService.getPaymentDetail(
			paymentPresentationMapper.toPaymentDetailRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			paymentPresentationMapper.toPaymentDetailResponseDto(responseServiceDto));

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
			paymentPresentationMapper.toPaymentDetailSearchRequestServiceDto(pageable, id, userId, paymentId,
				Boolean.FALSE));    // TODO userId
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			responseServiceDto.map(paymentPresentationMapper::toPaymentDetailResponseDto));

	}

}
