package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.paymentservice.application.PaymentAdminService;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.PaymentPresentationMapper;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailAdminResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipRefundResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketRefundResponseDto;
import com.boeingmerryho.business.paymentservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/payments")
public class PaymentAdminController {
	private final PaymentAdminService paymentAdminService;
	private final PaymentPresentationMapper paymentPresentationMapper;

	@PostMapping("/refund/tickets/{id}")
	public ResponseEntity<SuccessResponse<PaymentTicketRefundResponseDto>> refundTicketPayment(@PathVariable Long id) {
		PaymentTicketRefundResponseServiceDto responseServiceDto = paymentAdminService.refundTicketPayment(
			paymentPresentationMapper.toPaymentTicketRefundRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.TICKET_REFUNDED,
			paymentPresentationMapper.toPaymentTicketRefundResponseDto(responseServiceDto));
	}

	@PostMapping("/refund/memberships/{id}")
	public ResponseEntity<SuccessResponse<PaymentMembershipRefundResponseDto>> refundMembershipPayment(
		@PathVariable Long id) {
		PaymentMembershipRefundResponseServiceDto responseServiceDto = paymentAdminService.refundMembershipPayment(
			paymentPresentationMapper.toPaymentMembershipRefundRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.MEMBERSHIP_REFUNDED,
			paymentPresentationMapper.toPaymentMembershipRefundResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/tickets")
	public ResponseEntity<SuccessResponse<PaymentTicketCancelResponseDto>> cancelTicketPayment(@PathVariable Long id) {
		PaymentTicketCancelResponseServiceDto responseServiceDto = paymentAdminService.cancelTicketPayment(
			paymentPresentationMapper.toPaymentTicketCancelRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.TICKET_REFUND_REQUESTED,
			paymentPresentationMapper.toPaymentTicketCancelResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/memberships")
	public ResponseEntity<SuccessResponse<PaymentMembershipCancelResponseDto>> cancelMembershipPayment(
		@PathVariable Long id) {
		PaymentMembershipCancelResponseServiceDto responseServiceDto = paymentAdminService.cancelMembershipPayment(
			paymentPresentationMapper.toPaymentMembershipCancelRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.MEMBERSHIP_REFUND_REQUESTED,
			paymentPresentationMapper.toPaymentMembershipCancelResponseDto(responseServiceDto));
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<SuccessResponse<PaymentDetailAdminResponseDto>> getPaymentDetail(
		@PathVariable Long id) {

		PaymentDetailAdminResponseServiceDto responseServiceDto = paymentAdminService.getPaymentDetail(
			paymentPresentationMapper.toPaymentDetailRequestServiceDto(id));
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			paymentPresentationMapper.toPaymentDetailAdminResponseDto(responseServiceDto));

	}

	@GetMapping("/details")
	public ResponseEntity<SuccessResponse<Page<PaymentDetailAdminResponseDto>>> searchPaymentDetail(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "id", required = false) Long id,
		@RequestParam(value = "userId", required = false) Long userId,
		@RequestParam(value = "paymentId", required = false) Long paymentId,
		@RequestParam(value = "isDeleted", required = false) Boolean isDeleted
	) {

		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);

		Page<PaymentDetailAdminResponseServiceDto> responseServiceDto = paymentAdminService.searchPaymentDetail(
			paymentPresentationMapper.toPaymentDetailSearchRequestServiceDto(pageable, id, userId, paymentId,
				isDeleted));
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			responseServiceDto.map(paymentPresentationMapper::toPaymentDetailAdminResponseDto));

	}
}
