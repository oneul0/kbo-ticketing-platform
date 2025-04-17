package com.boeingmerryho.business.paymentservice.infrastructure.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.boeingmerryho.business.paymentservice.application.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveAdminRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.AccountInfo;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentMembership;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentTicket;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.KafkaProducerHelper;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.Ticket;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BankTransferStrategy implements PaymentStrategy {

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
		String accountNumber = "110-333-482102";
		String accountBank = "SHINHAN";
		LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
		String accountHolder = "BOEING_KBO_" + "USERNAME";
		paymentDetailRepository.save(PaymentDetail.builder()
			.payment(payment)
			.discountPrice(payment.getDiscountPrice())
			.method(getSupportedMethod())
			.discountAmount(payment.getTotalPrice() - payment.getDiscountPrice())
			.accountInfo(
				AccountInfo.builder()
					.accountNumber(accountNumber)
					.accountBank(accountBank)
					.dueDate(dueDate)
					.accountHolder(accountHolder)
					.build()
			)
			.build()
		);
		return paymentApplicationMapper.toPaymentReadyResponseServiceDto(
			payment.getId(),
			payment.getDiscountPrice(),
			accountNumber,
			accountBank,
			dueDate,
			accountHolder,
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
			PaymentDetail paymentDetail = paymentDetailRepository.save(
				PaymentDetail.builder()
					.payment(payment)
					.discountPrice(payment.getDiscountPrice())
					.method(PaymentMethod.BANK_TRANSFER)
					.discountAmount(payment.getTotalPrice() - payment.getDiscountPrice())
					.build()
			);
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
	public PaymentMethod getSupportedMethod() {
		return PaymentMethod.BANK_TRANSFER;
	}
}
