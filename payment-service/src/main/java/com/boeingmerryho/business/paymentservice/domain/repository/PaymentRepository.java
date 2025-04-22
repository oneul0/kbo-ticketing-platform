package com.boeingmerryho.business.paymentservice.domain.repository;

import java.util.List;
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

	PaymentTicket saveTicket(PaymentTicket paymentTicket);

	PaymentMembership saveMembership(PaymentMembership paymentMembership);

	List<PaymentTicket> findPaymentTicketByPaymentId(Long id);

	Optional<PaymentMembership> findPaymentMembershipByPaymentId(Long id);
}
