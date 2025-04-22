package com.boeingmerryho.business.paymentservice.infrastructure.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveAdminRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentRefundResponseServiceDto;
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
import com.boeingmerryho.business.paymentservice.presentation.code.PaymentErrorCode;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.Ticket;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BankTransferStrategy implements PaymentStrategy {

	private final PaymentFactory paymentFactory;
	private final PaymentRepository paymentRepository;
	private final KafkaProducerHelper kafkaProducerHelper;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	@Transactional
	public PaymentReadyResponseServiceDto pay(
		Payment payment,
		PaymentReadyRequestServiceDto requestDto
	) {
		PaymentDetail paymentDetail = paymentDetailRepository.save(paymentFactory.createDetail(
			payment,
			null,
			null,
			getSupportedMethod()
		));
		return paymentApplicationMapper.toPaymentReadyResponseServiceDto(
			payment.getId(),
			payment.getDiscountPrice(),
			paymentDetail.getAccountInfo(),
			null,
			payment.getCreatedAt()
		);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public PaymentApproveResponseServiceDto approve(
		Payment payment,
		PaymentApproveAdminRequestServiceDto requestServiceDto
	) {
		try {
			if (payment.getType() == PaymentType.TICKET) {
				List<Ticket> tickets = requestServiceDto.tickets();
				for (int i = 0; i < tickets.size(); i++) {
					paymentRepository.saveTicket(
						PaymentTicket.builder()
							.price(tickets.get(i).price())
							.ticketNo(tickets.get(i).no())
							.payment(payment)
							.build()
					);
				}
			}
			if (payment.getType() == PaymentType.MEMBERSHIP) {
				paymentRepository.saveMembership(
					PaymentMembership.builder()
						.price(payment.getTotalPrice())
						.membershipId(requestServiceDto.userId())
						.payment(payment)
						.build()
				);
			}
			payment.confirmPayment();

			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					if (payment.getType() == PaymentType.TICKET) {
						List<String> tickets = requestServiceDto.tickets().stream()
							.map(Ticket::no)
							.toList();
						kafkaProducerHelper.publishTicketPaymentSuccess(tickets);
					}
					if (payment.getType() == PaymentType.MEMBERSHIP) {
						kafkaProducerHelper.publishMembershipPaymentSuccess(
							requestServiceDto.userId(),
							requestServiceDto.membershipId()
						);
					}
				}
			});
			PaymentDetail paymentDetail = paymentDetailRepository.findPaymentDetailByPaymentId(
					requestServiceDto.paymentId())
				.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_DETAIL_NOT_FOUND));

			return paymentApplicationMapper.toPaymentApproveResponseServiceDto(paymentDetail);
		} catch (Exception e) {
			if (payment.getType() == PaymentType.TICKET) {
				List<String> tickets = requestServiceDto.tickets().stream()
					.map(Ticket::no)
					.toList();
				kafkaProducerHelper.publishTicketPaymentFailure(tickets);
			}
			if (payment.getType() == PaymentType.MEMBERSHIP) {
				kafkaProducerHelper.publishMembershipPaymentFailure(
					requestServiceDto.userId(),
					requestServiceDto.membershipId()
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
		return PaymentMethod.BANK_TRANSFER;
	}
}
