package com.boeingmerryho.business.seatservice.infrastructure.helper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.seatservice.domain.Membership;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipHelper {
	private final RedisTemplate<String, String> redisTemplate;

	public void checkMembership(LocalDate today, LocalDate requestDate, Long userId) {
		if (requestDate.isBefore(today)) {
			throw new GlobalException(SeatErrorCode.NOT_ACCESS_BEFORE_TODAY);
		}

		long daysBetween = ChronoUnit.DAYS.between(today, requestDate);
		if (Math.abs(daysBetween) > 7) {
			throw new GlobalException(SeatErrorCode.NOT_OVER_1_WEEK);
		}

		String membershipKey = createMembershipKey(userId);
		String membership = getMembership(membershipKey);
		log.info("{}", membership);

		if (requestDate.isEqual(today.plusDays(7))) {
			LocalTime now = LocalTime.now();

			switch (Membership.valueOf(membership)) {
				case NORMAL:
					if (now.isBefore(LocalTime.of(14, 0))) {
						throw new GlobalException(SeatErrorCode.NOT_OPEN_RESERVATION);
					}

				case GOLD:
					if (now.isBefore(LocalTime.of(12, 0))) {
						throw new GlobalException(SeatErrorCode.NOT_OPEN_RESERVATION);
					}

				case VIP:
					if (now.isBefore(LocalTime.of(12, 0))) {
						throw new GlobalException(SeatErrorCode.NOT_OPEN_RESERVATION);
					}

				case SVIP:
					if (now.isBefore(LocalTime.of(11, 0))) {
						throw new GlobalException(SeatErrorCode.NOT_OPEN_RESERVATION);
					}
			}
		}
	}

	private String createMembershipKey(Long userId) {
		StringBuilder membershipKey = new StringBuilder()
			.append("user:membership:info:")
			.append(userId);

		return membershipKey.toString();
	}

	private String getMembership(String membershipKey) {
		Map<Object, Object> membershipInfo = redisTemplate.opsForHash().entries(membershipKey);
		Object name = membershipInfo.get("name");

		return name != null ? name.toString() : "NORMAL";
	}
}