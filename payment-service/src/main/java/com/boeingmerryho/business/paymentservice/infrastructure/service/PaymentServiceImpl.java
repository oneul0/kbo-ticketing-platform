package com.boeingmerryho.business.paymentservice.infrastructure.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.PaymentService;
import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPaymentSession;
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
import com.boeingmerryho.business.paymentservice.domain.entity.KakaoPayInfo;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.KakaoApiClient;
import com.boeingmerryho.business.paymentservice.infrastructure.KakaoPaymentHelper;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	@Value("${kakaopay.cid}")
	String cid;

	@Value("${kakaopay.secret-key}")
	String secretKey;

	@Value("${kakaopay.auth-prefix}")
	String authPrefix;

	@Value("${kakaopay.redirect-url}")
	String redirectUrl;

	private final String APPROVE_PATH = "/api/v1/payments/approve";
	private final String CANCEL_PATH = "/api/v1/payments/cancel";
	private final String FAIL_PATH = "/kakao/payments/ready/fail";

	private final KakaoApiClient kakaoApiClient;
	private final PaymentRepository paymentRepository;
	private final KakaoPaymentHelper kakaoPaymentHelper;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Transactional(readOnly = true)
	public PaymentReadyResponseServiceDto pay(PaymentReadyRequestServiceDto requestServiceDto) {

		Payment payment = paymentRepository.findById(requestServiceDto.paymentId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

		KakaoPayReadyRequest request = KakaoPayReadyRequest.builder()
			.cid(cid)
			.partnerOrderId(payment.getId() + UUID.randomUUID().toString())
			.partnerUserId(requestServiceDto.userId().toString())
			.itemName(requestServiceDto.type())
			.quantity(requestServiceDto.tickets().size())
			.totalAmount(requestServiceDto.price())
			.vatAmount(0)
			.taxFreeAmount(0)
			.approvalUrl(redirectUrl + APPROVE_PATH)
			.cancelUrl(redirectUrl + CANCEL_PATH)
			.failUrl(redirectUrl + FAIL_PATH)
			.build();

		KakaoPayReadyResponse response = kakaoApiClient.callReady(request, secretKey, authPrefix);
		KakaoPaymentSession session = KakaoPaymentSession.builder()
			.cid(cid)
			.tid(response.tid())
			.partnerOrderId(request.partnerOrderId())
			.partnerUserId(request.partnerUserId())
			.tickets(requestServiceDto.tickets())
			.totalAmount(requestServiceDto.price())
			.quantity(requestServiceDto.tickets().size())
			.itemName(requestServiceDto.type())
			.createdAt(response.createdAt().toString())
			.build();

		kakaoPaymentHelper.savePaymentInfo(String.valueOf(requestServiceDto.paymentId()), session);

		return paymentApplicationMapper.toPaymentReadyResponseServiceDto(response);
	}

	@Transactional
	public PaymentApproveResponseServiceDto approvePayment(PaymentApproveRequestServiceDto requestServiceDto) {
		KakaoPaymentSession paymentSession = kakaoPaymentHelper.getPaymentInfo(
				String.valueOf(requestServiceDto.paymentId()))
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

		KakaoPayApproveRequest request = KakaoPayApproveRequest.builder()
			.cid(paymentSession.cid())
			.tid(paymentSession.tid())
			.partnerOrderId(paymentSession.partnerOrderId())
			.partnerUserId(paymentSession.partnerUserId())
			.pgToken(requestServiceDto.pgToken())
			.build();

		KakaoPayApproveResponse response = kakaoApiClient.callApprove(request, secretKey, authPrefix);

		Payment payment = paymentRepository.findById(requestServiceDto.paymentId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

		// kakaopay logic
		paymentDetailRepository.save(
			PaymentDetail.builder()
				.kakaoPayInfo(
					KakaoPayInfo.builder()
						.cid(paymentSession.cid())
						.tid(paymentSession.tid())
						.build()
				)
				.payment(payment)
				.discountPrice(payment.getDiscountPrice() - response.amount().discount())
				.method(PaymentMethod.KAKAOPAY)
				.discountAmount(payment.getTotalPrice() - payment.getDiscountPrice() - response.amount().discount())
				.build()
		);

		payment.confirmPayment();

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

	private void assertCancellablePayment(Payment payment) {
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
		PaymentDetail paymentDetail = paymentDetailRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
		return paymentApplicationMapper.toPaymentDetailResponseServiceDto(paymentDetail);
	}

	@Transactional(readOnly = true)
	public Page<PaymentDetailResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		Page<PaymentDetail> paymentDetails = paymentRepository.searchPaymentDetail(
			createSearchContext(requestServiceDto)
		);
		return paymentDetails.map(paymentApplicationMapper::toPaymentDetailResponseServiceDto);
	}

	private PaymentDetailSearchContext createSearchContext(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		return PaymentDetailSearchContext.builder()
			.id(requestServiceDto.id())
			.paymentId(requestServiceDto.paymentId())
			.customPageable(requestServiceDto.customPageable())
			.isDeleted(requestServiceDto.isDeleted())
			.build();
	}
}
