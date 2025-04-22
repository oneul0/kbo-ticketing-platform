package com.boeingmerryho.business.paymentservice.infrastructure.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayCancelRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayCancelResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.factory.KakaoPayRequestFactory;
import com.boeingmerryho.business.paymentservice.application.factory.PaymentSessionFactory;
import com.boeingmerryho.business.paymentservice.application.service.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentMembership;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentTicket;
import com.boeingmerryho.business.paymentservice.domain.factory.PaymentFactory;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;
import com.boeingmerryho.business.paymentservice.infrastructure.helper.KafkaProducerHelper;
import com.boeingmerryho.business.paymentservice.infrastructure.helper.KakaoApiClient;
import com.boeingmerryho.business.paymentservice.infrastructure.helper.PaySessionHelper;
import com.boeingmerryho.business.paymentservice.presentation.code.PaymentErrorCode;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.Ticket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPayStrategy implements PaymentStrategy {

	@Value("${kakaopay.cid}")
	String cid;

	@Value("${kakaopay.secret-key}")
	String secretKey;

	@Value("${kakaopay.auth-prefix}")
	String authPrefix;

	private final KakaoApiClient kakaoApiClient;
	private final PaymentFactory paymentFactory;
	private final PaySessionHelper paySessionHelper;
	private final PaymentRepository paymentRepository;
	private final KafkaProducerHelper kafkaProducerHelper;
	private final PaymentSessionFactory paymentSessionFactory;
	private final KakaoPayRequestFactory kakaoPayRequestFactory;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	@Transactional
	public PaymentReadyResponseServiceDto pay(
		Payment payment,
		PaymentReadyRequestServiceDto requestServiceDto
	) {
		KakaoPayReadyRequest request = kakaoPayRequestFactory.createReadyRequest(
			payment,
			requestServiceDto
		);
		KakaoPayReadyResponse response = kakaoApiClient.callReady(
			request,
			secretKey,
			authPrefix
		);
		PaymentSession session = paymentSessionFactory.createSession(
			payment,
			request,
			response,
			requestServiceDto
		);
		paySessionHelper.savePaymentInfo(String.valueOf(requestServiceDto.paymentId()), session);
		log.info("[Payment Ready Success] tid: {}, nextRedirectPcUrl: {}, createdAt: {}",
			response.tid(),
			response.nextRedirectPcUrl(),
			response.createdAt()
		);
		return paymentApplicationMapper.toPaymentReadyResponseServiceDto(
			payment.getId(),
			payment.getDiscountPrice(),
			null,
			response.nextRedirectPcUrl(),
			payment.getCreatedAt()
		);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public PaymentApproveResponseServiceDto approve(
		PaymentSession paymentSession,
		Payment payment,
		PaymentApproveRequestServiceDto requestServiceDto
	) {
		try {
			KakaoPayApproveRequest request = kakaoPayRequestFactory.createApproveRequest(
				paymentSession,
				requestServiceDto
			);
			KakaoPayApproveResponse response = kakaoApiClient.callApprove(
				request,
				secretKey,
				authPrefix
			);
			PaymentDetail paymentDetail = paymentDetailRepository.save(
				paymentFactory.createDetail(
					payment,
					paymentSession,
					response,
					getSupportedMethod()
				)
			);
			if (payment.getType() == PaymentType.TICKET) {
				List<Ticket> tickets = paymentSession.tickets();
				for (Ticket ticket : tickets) {
					paymentRepository.saveTicket(
						PaymentTicket.builder()
							.price(ticket.price())
							.ticketNo(ticket.no())
							.payment(payment)
							.build()
					);
				}
			}
			if (payment.getType() == PaymentType.MEMBERSHIP) {
				paymentRepository.saveMembership(
					PaymentMembership.builder()
						.price(paymentSession.totalAmount())
						.membershipId(paymentSession.membershipId())
						.payment(payment)
						.build()
				);
			}
			payment.confirmPayment();
			paySessionHelper.deletePaymentExpiredTime(String.valueOf(payment.getId()));

			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					if (payment.getType() == PaymentType.TICKET) {
						List<String> tickets = paymentSession.tickets().stream()
							.map(Ticket::no)
							.toList();
						kafkaProducerHelper.publishTicketPaymentSuccess(tickets);
					}
					if (payment.getType() == PaymentType.MEMBERSHIP) {
						kafkaProducerHelper.publishMembershipPaymentSuccess(
							requestServiceDto.userId(),
							paymentSession.membershipId()
						);
					}
				}
			});
			return paymentApplicationMapper.toPaymentApproveResponseServiceDto(paymentDetail);
		} catch (Exception e) {
			if (payment.getType() == PaymentType.TICKET) {
				List<String> tickets = paymentSession.tickets().stream()
					.map(Ticket::no)
					.toList();
				kafkaProducerHelper.publishTicketPaymentFailure(tickets);
			}
			if (payment.getType() == PaymentType.MEMBERSHIP) {
				kafkaProducerHelper.publishMembershipPaymentFailure(
					requestServiceDto.userId(),
					paymentSession.membershipId()
				);
			}
			throw e;
		}
	}

	@Override
	@Transactional
	public PaymentRefundResponseServiceDto refund(
		PaymentDetail paymentDetail
	) {
		KakaoPayCancelRequest request = kakaoPayRequestFactory.createCancelRequest(paymentDetail);
		KakaoPayCancelResponse response = kakaoApiClient.callCancel(
			request,
			secretKey,
			authPrefix
		);
		log.info("[Payment Cancel Success] tid: {}, price: {}, createdAt: {}, approvedAt: {}",
			response.tid(),
			response.approvedCancelAmount().total(),
			response.createdAt(),
			response.approvedAt()
		);
		paymentDetail.getPayment().refundPayment();

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				if (paymentDetail.getPayment().getType() == PaymentType.TICKET) {
					List<String> tickets = paymentRepository.findPaymentTicketByPaymentId(
						paymentDetail.getPayment().getId()
					).stream().map(PaymentTicket::getTicketNo).toList();
					kafkaProducerHelper.publishTicketRefundSuccess(tickets);
				}
				if (paymentDetail.getPayment().getType() == PaymentType.MEMBERSHIP) {
					PaymentMembership paymentMembership = paymentRepository.findPaymentMembershipByPaymentId(
						paymentDetail.getPayment().getId()
					).orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_MEMBERSHIP_NOT_FOUND));

					kafkaProducerHelper.publishMembershipRefundSuccess(
						paymentDetail.getPayment().getUserId(),
						paymentMembership.getMembershipId()
					);
				}
			}
		});

		return paymentApplicationMapper.toPaymentRefundResponseServiceDto(paymentDetail);
	}

	@Override
	public PaymentMethod getSupportedMethod() {
		return PaymentMethod.KAKAOPAY;
	}

}
