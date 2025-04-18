package com.boeingmerryho.business.paymentservice.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
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
import com.boeingmerryho.business.paymentservice.presentation.dto.PaymentPresentationMapper;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentApproveRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentReadyRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentApproveResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentReadyResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketCancelResponseDto;
import com.boeingmerryho.business.paymentservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.entity.UserRoleType;
import io.github.boeingmerryho.commonlibrary.interceptor.RequiredRoles;
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
	@RequiredRoles({UserRoleType.NORMAL, UserRoleType.SENIOR})
	public ResponseEntity<SuccessResponse<PaymentReadyResponseDto>> pay(
		@RequestAttribute Long userId,
		@RequestBody @Valid PaymentReadyRequestDto requestDto
	) {
		PaymentReadyResponseServiceDto responseServiceDto = paymentService.pay(
			paymentPresentationMapper.toPaymentReadyRequestServiceDto(userId, requestDto)
		);
		return SuccessResponse.of(PaymentSuccessCode.PAYMENT_READY_REQUESTED,
			paymentPresentationMapper.toPaymentReadyResponseDto(responseServiceDto));
	}

	@PostMapping("/approve")
	@RequiredRoles({UserRoleType.NORMAL, UserRoleType.SENIOR})
	public ResponseEntity<SuccessResponse<PaymentApproveResponseDto>> approvePayment(
		@RequestAttribute Long userId,
		@RequestParam("pg_token") String pgToken,
		@RequestBody PaymentApproveRequestDto requestDto
	) {
		PaymentApproveResponseServiceDto responseServiceDto = paymentService.approvePayment(
			paymentPresentationMapper.toPaymentApproveRequestServiceDto(userId, pgToken, requestDto)
		);
		return SuccessResponse.of(PaymentSuccessCode.PAYMENT_APPROVED,
			paymentPresentationMapper.toPaymentApproveResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/tickets")
	@RequiredRoles({UserRoleType.NORMAL, UserRoleType.SENIOR})
	public ResponseEntity<SuccessResponse<PaymentTicketCancelResponseDto>> cancelTicketPayment(
		@RequestAttribute Long userId,
		@PathVariable Long id
	) {
		PaymentTicketCancelResponseServiceDto responseServiceDto = paymentService.cancelTicketPayment(
			paymentPresentationMapper.toPaymentTicketCancelRequestServiceDto(userId, id)
		);
		return SuccessResponse.of(PaymentSuccessCode.TICKET_REFUND_REQUESTED,
			paymentPresentationMapper.toPaymentTicketCancelResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/memberships")
	@RequiredRoles({UserRoleType.NORMAL, UserRoleType.SENIOR})
	public ResponseEntity<SuccessResponse<PaymentMembershipCancelResponseDto>> cancelMembershipPayment(
		@RequestAttribute Long userId,
		@PathVariable Long id
	) {
		PaymentMembershipCancelResponseServiceDto responseServiceDto = paymentService.cancelMembershipPayment(
			paymentPresentationMapper.toPaymentMembershipCancelRequestServiceDto(userId, id)
		);
		return SuccessResponse.of(PaymentSuccessCode.MEMBERSHIP_REFUND_REQUESTED,
			paymentPresentationMapper.toPaymentMembershipCancelResponseDto(responseServiceDto));
	}

	@GetMapping("/details/{id}")
	@RequiredRoles({UserRoleType.NORMAL, UserRoleType.SENIOR})
	public ResponseEntity<SuccessResponse<PaymentDetailResponseDto>> getPaymentDetail(
		@RequestAttribute Long userId,
		@PathVariable Long id
	) {
		PaymentDetailResponseServiceDto responseServiceDto = paymentService.getPaymentDetail(
			paymentPresentationMapper.toPaymentDetailRequestServiceDto(userId, id)
		);
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			paymentPresentationMapper.toPaymentDetailResponseDto(responseServiceDto));
	}

	@GetMapping("/details")
	@RequiredRoles({UserRoleType.NORMAL, UserRoleType.SENIOR})
	public ResponseEntity<SuccessResponse<Page<PaymentDetailResponseDto>>> searchPaymentDetail(
		@RequestAttribute Long userId,
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "id", required = false) Long id,
		@RequestParam(value = "paymentId", required = false) Long paymentId
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		Page<PaymentDetailResponseServiceDto> responseServiceDto = paymentService.searchPaymentDetail(
			paymentPresentationMapper.toPaymentDetailSearchRequestServiceDto(
				pageable,
				id,
				userId,
				paymentId,
				Boolean.FALSE)
		);
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			responseServiceDto.map(paymentPresentationMapper::toPaymentDetailResponseDto));
	}

}
