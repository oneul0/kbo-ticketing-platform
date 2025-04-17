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

import com.boeingmerryho.business.paymentservice.application.PaymentAdminService;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.PaymentPresentationMapper;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.PaymentApproveRequestDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentApproveResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentDetailAdminResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentMembershipRefundResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketCancelResponseDto;
import com.boeingmerryho.business.paymentservice.presentation.dto.response.PaymentTicketRefundResponseDto;
import com.boeingmerryho.business.paymentservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.entity.UserRoleType;
import io.github.boeingmerryho.commonlibrary.interceptor.RequiredRoles;
import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/payments")
public class PaymentAdminController {
	private final PaymentAdminService paymentAdminService;
	private final PaymentPresentationMapper paymentPresentationMapper;

	@PostMapping("/approve")
	@RequiredRoles({
		UserRoleType.MANAGER,
		UserRoleType.ADMIN
	})
	public ResponseEntity<SuccessResponse<PaymentApproveResponseDto>> approvePayment(
		@RequestAttribute Long userId,
		@RequestBody PaymentApproveRequestDto requestDto
	) {
		PaymentApproveResponseServiceDto responseServiceDto = paymentAdminService.approvePayment(
			paymentPresentationMapper.toPaymentApproveAdminRequestServiceDto(userId, requestDto)
		);
		return SuccessResponse.of(PaymentSuccessCode.PAYMENT_APPROVED,
			paymentPresentationMapper.toPaymentApproveResponseDto(responseServiceDto));
	}

	@PostMapping("/refund/tickets/{id}")
	@RequiredRoles({
		UserRoleType.MANAGER,
		UserRoleType.ADMIN
	})
	public ResponseEntity<SuccessResponse<PaymentTicketRefundResponseDto>> refundTicketPayment(
		@RequestAttribute Long userId,
		@PathVariable Long id
	) {
		PaymentTicketRefundResponseServiceDto responseServiceDto = paymentAdminService.refundTicketPayment(
			paymentPresentationMapper.toPaymentTicketRefundRequestServiceDto(userId, id)
		);
		return SuccessResponse.of(PaymentSuccessCode.TICKET_REFUNDED,
			paymentPresentationMapper.toPaymentTicketRefundResponseDto(responseServiceDto));
	}

	@PostMapping("/refund/memberships/{id}")
	@RequiredRoles({
		UserRoleType.MANAGER,
		UserRoleType.ADMIN
	})
	public ResponseEntity<SuccessResponse<PaymentMembershipRefundResponseDto>> refundMembershipPayment(
		@RequestAttribute Long userId,
		@PathVariable Long id
	) {
		PaymentMembershipRefundResponseServiceDto responseServiceDto = paymentAdminService.refundMembershipPayment(
			paymentPresentationMapper.toPaymentMembershipRefundRequestServiceDto(userId, id)
		);
		return SuccessResponse.of(PaymentSuccessCode.MEMBERSHIP_REFUNDED,
			paymentPresentationMapper.toPaymentMembershipRefundResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/tickets")
	@RequiredRoles({
		UserRoleType.MANAGER,
		UserRoleType.ADMIN
	})
	public ResponseEntity<SuccessResponse<PaymentTicketCancelResponseDto>> cancelTicketPayment(
		@RequestAttribute Long userId,
		@PathVariable Long id
	) {
		PaymentTicketCancelResponseServiceDto responseServiceDto = paymentAdminService.cancelTicketPayment(
			paymentPresentationMapper.toPaymentTicketCancelRequestServiceDto(userId, id)
		);
		return SuccessResponse.of(PaymentSuccessCode.TICKET_REFUND_REQUESTED,
			paymentPresentationMapper.toPaymentTicketCancelResponseDto(responseServiceDto));
	}

	@PutMapping("/{id}/cancel/memberships")
	@RequiredRoles({
		UserRoleType.MANAGER,
		UserRoleType.ADMIN
	})
	public ResponseEntity<SuccessResponse<PaymentMembershipCancelResponseDto>> cancelMembershipPayment(
		@RequestAttribute Long userId,
		@PathVariable Long id
	) {
		PaymentMembershipCancelResponseServiceDto responseServiceDto = paymentAdminService.cancelMembershipPayment(
			paymentPresentationMapper.toPaymentMembershipCancelRequestServiceDto(userId, id)
		);
		return SuccessResponse.of(PaymentSuccessCode.MEMBERSHIP_REFUND_REQUESTED,
			paymentPresentationMapper.toPaymentMembershipCancelResponseDto(responseServiceDto));
	}

	@GetMapping("/details/{id}")
	@RequiredRoles({
		UserRoleType.MANAGER,
		UserRoleType.ADMIN
	})
	public ResponseEntity<SuccessResponse<PaymentDetailAdminResponseDto>> getPaymentDetail(
		@RequestAttribute Long userId,
		@PathVariable Long id
	) {
		PaymentDetailAdminResponseServiceDto responseServiceDto = paymentAdminService.getPaymentDetail(
			paymentPresentationMapper.toPaymentDetailRequestServiceDto(userId, id)
		);
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			paymentPresentationMapper.toPaymentDetailAdminResponseDto(responseServiceDto));
	}

	@GetMapping("/details")
	@RequiredRoles({
		UserRoleType.MANAGER,
		UserRoleType.ADMIN
	})
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
			paymentPresentationMapper.toPaymentDetailSearchRequestServiceDto(pageable, id, userId, paymentId, isDeleted)
		);
		return SuccessResponse.of(PaymentSuccessCode.FETCHED_PAYMENT_DETAIL,
			responseServiceDto.map(paymentPresentationMapper::toPaymentDetailAdminResponseDto));

	}
}
