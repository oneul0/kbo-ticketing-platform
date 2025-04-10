package com.boeingmerryho.business.paymentservice.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.exception.PaymentException;
import com.boeingmerryho.business.paymentservice.infrastructure.KakaoFeignService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	@Value("${kakaopay.cid}")
	String cid;

	@Value("${kakaopay.secret-key}")
	String secretKey;

	@Value("${kakaopay.auth-prefix}")
	String authPrefix;

	@Value("${kakaopay.redirect-url}")
	String redirectUrl;

	private final PaymentRepository paymentRepository;
	private final KakaoFeignService kakaoFeignService;
	private final PaymentApplicationMapper paymentApplicationMapper;

	public PaymentReadyResponseServiceDto readyPayment(PaymentReadyRequestServiceDto requestServiceDto) {

		// Payment payment = paymentRepository.findById(requestServiceDto.paymentId())
		// 	.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		//
		// KakaoPayReadyRequest request = KakaoPayReadyRequest.builder()
		// 	.cid(cid)
		// 	.partner_order_id(String.valueOf(requestServiceDto.paymentId()))
		// 	.partner_user_id(String.valueOf(requestServiceDto.userId()))
		// 	.item_name(String.valueOf(payment.getType()))
		// 	.quantity(requestServiceDto.tickets().size())
		// 	.total_amount(payment.getDiscountPrice())
		// 	.tax_free_amount(0)
		// 	.approval_url(redirectUrl + "/api/v1/payments/approve")
		// 	.cancel_url(redirectUrl + "/kakao/payments/ready/cancel")
		// 	.fail_url(redirectUrl + "/kakao/payments/ready/fail")
		// 	.build();

		KakaoPayReadyRequest request = KakaoPayReadyRequest.builder()
			.cid(cid)
			.partnerOrderId("order_id1")
			.partnerUserId("user_id1")
			.itemName("item_id")
			.quantity(2)
			.totalAmount(1000)
			.taxFreeAmount(0)
			.approvalUrl(redirectUrl + "/api/v1/payments/approve")
			.cancelUrl(redirectUrl + "/kakao/payments/ready/cancel")
			.failUrl(redirectUrl + "/kakao/payments/ready/fail")
			.build();

		KakaoPayReadyResponse response = kakaoFeignService.ready(authPrefix + secretKey, "application/json", request);

		// TODO Redis에 결제 승인 단계에서 필요한 정보들 미리 저장해두기
		return paymentApplicationMapper.toPaymentReadyResponseServiceDto(response);
	}

	public PaymentApproveResponseServiceDto approvePayment(PaymentApproveRequestServiceDto requestServiceDto) {
		// TODO Redis에서 결제 승인에 필요한 정보들 불러오기
		KakaoPayApproveRequest request = KakaoPayApproveRequest.builder()
			.cid(cid)
			.tid("T7f7981a1b530bd8f5d0")
			.partnerOrderId("order_id1")
			.partnerUserId("user_id1")
			.pgToken(requestServiceDto.pgToken())
			.build();

		KakaoPayApproveResponse response = kakaoFeignService.approve(authPrefix + secretKey, "application/json",
			request);

		// TODO DB에 결제 내역 저장하기
		return paymentApplicationMapper.toPaymentApproveResponseServiceDto(response);
	}

	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	private static void assertCancellablePayment(Payment payment) {

		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
	}

	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Transactional(readOnly = true)
	public PaymentDetailResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto) {
		PaymentDetail paymentDetail = paymentRepository.findPaymentDetailByIdAndIsDeleted(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
		return paymentApplicationMapper.toPaymentDetailResponseServiceDto(paymentDetail);
	}

	@Transactional(readOnly = true)
	public Page<PaymentDetailResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		Page<PaymentDetail> paymentDetails = paymentRepository.searchPaymentDetail(
			createSearchContext(
				requestServiceDto.id(),
				requestServiceDto.paymentId(),
				requestServiceDto.customPageable(),
				requestServiceDto.isDeleted()
			)
		);
		return paymentDetails.map(paymentApplicationMapper::toPaymentDetailResponseServiceDto);
	}

	private PaymentDetailSearchContext createSearchContext(
		Long id,
		Long paymentId,
		Pageable customPageable,
		Boolean isDeleted
	) {
		return PaymentDetailSearchContext.builder()
			.id(id)
			.paymentId(paymentId)
			.customPageable(customPageable)
			.isDeleted(isDeleted)
			.build();
	}
}
