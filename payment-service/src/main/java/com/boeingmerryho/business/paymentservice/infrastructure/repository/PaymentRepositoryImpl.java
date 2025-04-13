package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentMembership;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentTicket;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
	private final PaymentJpaRepository paymentJpaRepository;
	private final PaymentQueryRepository paymentQueryRepository;
	private final PaymentTicketJpaRepository paymentTicketJpaRepository;
	private final PaymentMembershipJpaRepository paymentMembershipJpaRepository;

	@Override
	public Payment save(Payment payment) {
		return paymentJpaRepository.save(payment);
	}

	@Override
	public Optional<Payment> findById(Long id) {
		return paymentJpaRepository.findById(id);
	}

	@Override
	public Page<PaymentDetail> searchPaymentDetail(PaymentDetailSearchContext context) {
		return paymentQueryRepository.searchPaymentDetail(context);
	}

	@Override
	public Optional<PaymentTicket> findByPaymentTicketId(Long id) {
		return paymentTicketJpaRepository.findById(id);
	}

	@Override
	public Optional<PaymentMembership> findByPaymentMembershipId(Long id) {
		return paymentMembershipJpaRepository.findById(id);
	}

	@Override
	public PaymentTicket saveTicket(PaymentTicket paymentTicket) {
		return paymentTicketJpaRepository.save(paymentTicket);
	}

	@Override
	public PaymentMembership saveMembership(PaymentMembership paymentMembership) {
		return paymentMembershipJpaRepository.save(paymentMembership);
	}

}
