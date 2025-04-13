package com.boeingmerryho.business.paymentservice.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentMembership;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentTicket;

public interface PaymentRepository {
	Payment save(Payment payment);

	Optional<Payment> findById(Long id);

	Page<PaymentDetail> searchPaymentDetail(PaymentDetailSearchContext searchContext);

	Optional<PaymentTicket> findByPaymentTicketId(Long id);

	Optional<PaymentMembership> findByPaymentMembershipId(Long id);

}
