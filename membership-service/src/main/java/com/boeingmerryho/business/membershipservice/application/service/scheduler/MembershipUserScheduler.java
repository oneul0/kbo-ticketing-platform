package com.boeingmerryho.business.membershipservice.application.service.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.membershipservice.infrastructure.repository.MembershipJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipUserScheduler {

	private final MembershipJpaRepository membershipJpaRepository;

	@Transactional
	@Scheduled(cron = "0 0 0 1 1 *")
	public void deactivateLastSeasonMembershipUsers() {
		int currentYear = LocalDate.now().getYear();
		int previousSeason = currentYear - 1;

		int updatedCount = membershipJpaRepository.bulkDeactivateBySeason(previousSeason);
		System.out.println("[스케줄러] 시즌 " + previousSeason + " 멤버십 사용자 " + updatedCount + "명 비활성화 완료");
	}
}
