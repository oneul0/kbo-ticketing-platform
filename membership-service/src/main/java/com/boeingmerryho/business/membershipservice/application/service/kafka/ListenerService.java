package com.boeingmerryho.business.membershipservice.application.service.kafka;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.infrastructure.helper.MembershipListenerHelper;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipPaymentEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListenerService {

	private final MembershipListenerHelper membershipListenerHelper;

	@Transactional
	public void membershipSucceed(MembershipPaymentEvent response) {
		membershipListenerHelper.handleMembershipSuccess(response.userId());
	}
}
