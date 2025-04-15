package com.boeingmerryho.business.membershipservice.application.service.kafka;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.application.dto.kafka.MembershipPaymentEvent;
import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipListenerHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListenerService {

	private final MembershipListenerHelper membershipListenerHelper;

	@Transactional
	public void paymentSucceed(MembershipPaymentEvent response) {
		membershipListenerHelper.handleMembershipSuccess(response.userId());
	}

	public void paymentFailure(MembershipPaymentEvent response) {
	}

	public void paymentCancel(MembershipPaymentEvent response) {
	}
}
