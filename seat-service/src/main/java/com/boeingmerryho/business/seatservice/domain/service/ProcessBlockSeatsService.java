package com.boeingmerryho.business.seatservice.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatsProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketMatchDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketSeatDto;
import com.boeingmerryho.business.seatservice.domain.Membership;
import com.boeingmerryho.business.seatservice.domain.ReservationStatus;
import com.boeingmerryho.business.seatservice.exception.MatchErrorCode;
import com.boeingmerryho.business.seatservice.exception.MembershipErrorCode;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;
import com.boeingmerryho.business.seatservice.infrastructure.helper.MatchHelper;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatCommonHelper;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessBlockSeatsService {
	private final MatchHelper matchHelper;
	private final RedissonClient redissonClient;
	private final SeatCommonHelper seatCommonHelper;
	private final SeatApplicationMapper seatApplicationMapper;
	private final RedisTemplate<String, String> redisTemplate;

	private static final long LOCK_TIMEOUT = 10;
	private static final long LOCK_WAIT_TIME = 3;

	public ToTicketMatchDto getMatchInfo(CacheSeatsProcessServiceRequestDto serviceDto, LocalDate today) {
		Map<String, Object> match = matchHelper.getByMatchId(serviceDto.matchId());

		if (!Objects.equals(match.get("match_day").toString(), serviceDto.date().toString())) {
			log.error("Request Date : {} - Match Date : {}", serviceDto.date(), match.get("match_day"));
			throw new GlobalException(MatchErrorCode.NOT_MATCH_DATE);
		}

		LocalTime matchTime = LocalTime.parse(match.get("match_time").toString());

		if (serviceDto.date().isEqual(today) && LocalTime.now().isAfter(matchTime)) {
			throw new GlobalException(SeatErrorCode.START_GAME_SEAT_NOT_PROCESS);
		}

		return new ToTicketMatchDto(
			match.get("id").toString(),
			match.get("home_team_id").toString(),
			match.get("away_team_id").toString(),
			match.get("match_day").toString(),
			match.get("match_time").toString(),
			match.get("stadium_id").toString()
		);
	}

	public void getBlockSeats(
		List<RLock> locks,
		RSet<String> blockSeats,
		List<String> requestSeats,
		CacheSeatsProcessServiceRequestDto serviceDto
	) {
		String cacheSeatKeyFront = createCacheSeatKeyFront(serviceDto.date(), serviceDto.blockId());

		for (CacheSeatProcessServiceRequestDto requestSeat : serviceDto.serviceRequestSeatInfos()) {
			String cacheSeatKey = createCacheSeatKey(
				cacheSeatKeyFront,
				requestSeat.column(),
				requestSeat.row()
			);

			if (!blockSeats.contains(cacheSeatKey)) {
				log.error("블록 내 존재하지 않는 좌석 요청 : {}", cacheSeatKey);
				throw new GlobalException(SeatErrorCode.NOT_FOUND_SEAT);
			}

			requestSeats.add(cacheSeatKey);
			locks.add(redissonClient.getLock(createLockKey(cacheSeatKey)));
		}
	}

	public void validateSeatReservation(Long userId, CacheSeatsProcessServiceRequestDto request, LocalDate today) {
		if (request.serviceRequestSeatInfos().size() > 4) {
			throw new GlobalException(SeatErrorCode.NOT_EXCEED_4_SEAT);
		}

		if (request.date().isBefore(today)) {
			throw new GlobalException(SeatErrorCode.NOT_ACCESS_BEFORE_TODAY);
		}

		long daysBetween = ChronoUnit.DAYS.between(today, request.date());
		if (Math.abs(daysBetween) > 7) {
			throw new GlobalException(SeatErrorCode.NOT_OVER_1_WEEK);
		}

		String membershipKey = createMembershipKey(userId);
		log.info("membershipKey: {}", membershipKey);
		String membership = getMembership(membershipKey);

		if (request.date().isEqual(today.plusDays(7))) {
			checkReservationTime(membership);
		}
	}

	public void checkReservationTime(String membership) {
		LocalTime now = LocalTime.now();

		Membership memberType;
		try {
			memberType = Membership.valueOf(membership.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new GlobalException(MembershipErrorCode.INVALID_MEMBERSHIP);
		}

		LocalTime reservationOpenTime = switch (memberType) {
			case NORMAL, SENIOR -> (LocalTime.of(14, 0));
			case GOLD, VIP -> (LocalTime.of(12, 0));
			case SVIP -> (LocalTime.of(11, 0));
		};

		if (now.isBefore(reservationOpenTime)) {
			throw new GlobalException(SeatErrorCode.NOT_OPEN_RESERVATION);
		}
	}

	public void processSeatLocksAndUpdate(
		Long userId,
		List<RLock> locks,
		List<String> requestSeats,
		List<ToTicketSeatDto> seatInfos
	) {
		try {
			acquireLocks(locks);

			for (String requestSeat : requestSeats) {
				RBucket<Map<String, String>> seatBucketKey = redissonClient.getBucket(requestSeat);
				Map<String, String> seatBucketValue = seatBucketKey.get();

				if (
					seatBucketValue == null || !seatBucketValue.get("status").equals(ReservationStatus.AVAILABLE.name())
				) {
					throw new GlobalException(SeatErrorCode.ALREADY_PROCESS_SEAT);
				}

				updateSeatStatus(userId, seatBucketValue, seatBucketKey);

				ToTicketSeatDto seatInfo = parseSeatBucket(seatBucketKey.getName(), seatBucketValue);
				seatInfos.add(seatInfo);
			}

		} catch (Exception e) {
			throw new GlobalException(SeatErrorCode.FAILED_PROCESS_SEAT);
		} finally {
			releaseLocks(locks);
		}
	}

	private void acquireLocks(List<RLock> locks) throws InterruptedException {
		for (RLock lock : locks) {
			if (!lock.tryLock(LOCK_WAIT_TIME, LOCK_TIMEOUT, TimeUnit.SECONDS)) {
				log.error("⛔️ 락 획득 실패");
				throw new GlobalException(SeatErrorCode.FAILED_GET_LOCK);
			}
		}
	}

	private void releaseLocks(List<RLock> locks) {
		for (RLock lock : locks) {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	private void updateSeatStatus(
		Long userId,
		Map<String, String> seatBucketValue,
		RBucket<Map<String, String>> seatBucketKey
	) {
		seatBucketValue.put("status", ReservationStatus.PROCESSING.name());
		seatBucketValue.put("userId", String.valueOf(userId));
		seatBucketValue.put("createdAt", LocalDateTime.now().toString());
		seatBucketValue.put("expiredAt", LocalDateTime.now().plusMinutes(9).toString());

		seatBucketKey.set(seatBucketValue);
	}

	public RSet<String> getBlocks(String cacheBlockKey) {
		RSet<String> blockSeats = redissonClient.getSet(cacheBlockKey);
		if (!blockSeats.isExists()) {
			throw new GlobalException(SeatErrorCode.NOT_FOUND_BLOCK);
		}

		return blockSeats;
	}

	private String createMembershipKey(Long userId) {
		return String.format("user:membership:info:%d", userId);
	}

	private String getMembership(String membershipKey) {
		Object name;

		Map<Object, Object> membershipInfo = redisTemplate.opsForHash().entries(membershipKey);

		name = membershipInfo.get("name");

		if (name == null) {
			name = Membership.NORMAL.name();
			// throw new GlobalException(MembershipErrorCode.NOT_FOUND_MEMBERSHIP);
		}

		return name.toString();
	}

	private String createLockKey(String cacheSeatKey) {
		StringBuilder cacheLockKey = new StringBuilder()
			.append(cacheSeatKey)
			.append(":lock");

		return cacheLockKey.toString();
	}

	private String createCacheSeatKeyFront(LocalDate date, Integer blockId) {
		StringBuilder keyFront = new StringBuilder()
			.append(seatCommonHelper.seatPrefix)
			.append(date)
			.append(":")
			.append(blockId)
			.append(":");

		return keyFront.toString();
	}

	private String createCacheSeatKey(String cacheSeatKeyFront, Integer column, Integer row) {
		return String.format("%s%d:%d", cacheSeatKeyFront, column, row);
	}

	private ToTicketSeatDto parseSeatBucket(String seatBucketKey, Map<String, String> seatBucketValue) {
		String[] parts = seatBucketKey.split(":");

		return seatApplicationMapper.toTicketSeatDto(
			seatBucketValue.get("id"),
			seatBucketValue.get("userId"),
			parts[2],
			parts[3],
			parts[4],
			seatBucketValue.get("price"),
			seatBucketValue.get("createdAt"),
			seatBucketValue.get("expiredAt")
		);
	}
}