package com.boeingmerryho.business.seatservice.domain.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.infrastructure.helper.MatchHelper;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatCommonHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoCleanCacheSeatsService {
	private final MatchHelper matchHelper;
	private final RedissonClient redissonClient;
	private final SeatCommonHelper seatCommonHelper;

	public void afterMatchStart() {
		LocalDate today = LocalDate.now();

		Map<String, Object> match = matchHelper.getByMatchDate(today);
		if (match == null) {
			log.error("날짜: {}, 경기가 존재하지 않습니다.", today);
			return;
		}

		LocalTime matchStartTime = LocalTime.parse(match.get("match_time").toString());
		if (LocalTime.now().isBefore(matchStartTime)) {
			log.error("경기 시작 전이므로 좌석을 삭제할 수 없습니다.");
			return;
		}

		Iterable<String> blockKeys = redissonClient.getKeys().getKeysByPattern(
			seatCommonHelper.seatKeyPrefix + today + ":*"
		);

		for (String blockKey : blockKeys) {
			RList<String> seatKeys = redissonClient.getList(blockKey);

			for (String seatKey : seatKeys) {
				redissonClient.getBucket(seatKey).delete();
			}

			seatKeys.delete();
		}

		log.info("날짜: {}, 좌석 정보가 모두 삭제되었습니다.", today);
	}
}