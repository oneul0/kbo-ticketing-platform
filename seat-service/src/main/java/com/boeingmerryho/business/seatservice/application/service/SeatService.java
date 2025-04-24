package com.boeingmerryho.business.seatservice.application.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheBlockServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatsProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketMatchDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketSeatDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheBlockServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheSeatServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.Membership;
import com.boeingmerryho.business.seatservice.domain.ReservationStatus;
import com.boeingmerryho.business.seatservice.domain.service.GetCacheBlockSeatsService;
import com.boeingmerryho.business.seatservice.domain.service.ProcessBlockSeatsService;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;
import com.boeingmerryho.business.seatservice.infrastructure.helper.MembershipHelper;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatCommonHelper;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {
	private final RedissonClient redissonClient;
	private final SeatCommonHelper seatCommonHelper;
	private final MembershipHelper membershipHelper;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final RedisTemplate<String, String> redisTemplate;
	private final SeatApplicationMapper seatApplicationMapper;
	private final ProcessBlockSeatsService processBlockSeatsService;
	private final GetCacheBlockSeatsService getCacheBlockSeatsService;

	@Transactional(readOnly = true)
	public CacheBlockServiceResponseDto getBlockSeats(Long userId, CacheBlockServiceRequestDto request) {
		LocalDate today = LocalDate.now();

		membershipHelper.checkMembership(today, request.date(), userId);

		RSet<String> blockSeats = getCacheBlockSeatsService.getBlocks(request);
		List<CacheSeatServiceResponseDto> seats = getCacheBlockSeatsService.getBlockSeats(blockSeats);

		return seatApplicationMapper.toCacheBlockServiceResponseDto(request.blockId(), seats);
	}

	@Transactional
	public void processBlockSeats(Long userId, CacheSeatsProcessServiceRequestDto request) {
		if (request.serviceRequestSeatInfos().size() > 4) {
			throw new GlobalException(SeatErrorCode.NOT_EXCEED_4_SEAT);
		}

		LocalDate today = LocalDate.now();

		if (request.date().isBefore(today)) {
			throw new GlobalException(SeatErrorCode.NOT_ACCESS_BEFORE_TODAY);
		}

		long daysBetween = ChronoUnit.DAYS.between(today, request.date());
		if (Math.abs(daysBetween) > 7) {
			throw new GlobalException(SeatErrorCode.NOT_OVER_1_WEEK);
		}

		String membershipKey = createMembershipKey(userId);
		String membership = getMembership(membershipKey);
		log.info("{}", membership);

		if (request.date().isEqual(today.plusDays(7))) {
			LocalTime now = LocalTime.now();

			switch (Membership.valueOf(membership)) {
				case NORMAL:
					if (now.isBefore(LocalTime.of(14, 0))) {
						throw new GlobalException(SeatErrorCode.NOT_OPEN_RESERVATION);
					}

				case SENIOR:
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

		ToTicketMatchDto matchInfo = processBlockSeatsService.getMatchInfo(request, today);

		String cacheBlockKey = seatCommonHelper.createCacheBlockKey(request.blockId(), request.date());
		RList<String> blockSeats = processBlockSeatsService.getBlockSeats(cacheBlockKey);

		List<RLock> locks = new ArrayList<>();
		List<String> requestSeats = new ArrayList<>();
		List<ToTicketSeatDto> seatInfos = new ArrayList<>();

		String cacheSeatKeyFront = createCacheSeatKeyFront(request.date(), request.blockId());

		for (CacheSeatProcessServiceRequestDto requestSeat : request.serviceRequestSeatInfos()) {
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

		try {
			for (RLock lock : locks) {
				if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
					log.error("⛔️ 락 획득 실패");
					throw new GlobalException(SeatErrorCode.FAILED_GET_LOCK);
				}
			}

			for (String requestSeat : requestSeats) {
				RBucket<Map<String, String>> seatBucketKey = redissonClient.getBucket(requestSeat);
				Map<String, String> seatBucketValue = seatBucketKey.get();

				if (seatBucketValue == null) {
					throw new GlobalException(SeatErrorCode.NOT_FOUND_SEAT);
				}

				String requestSeatStatus = seatBucketValue.get("status");
				if (!requestSeatStatus.equals(ReservationStatus.AVAILABLE.name())) {
					throw new GlobalException(SeatErrorCode.ALREADY_PROCESS_SEAT);
				}

				boolean requestSeatIsSenior = Boolean.parseBoolean(seatBucketValue.get("isSenior"));
				if (requestSeatIsSenior) {
					if (!membership.equals(Membership.SENIOR.name())) {
						log.error("시니어 좌석이므로 권한이 존재하지 않습니다.");
						throw new GlobalException(SeatErrorCode.PROCESS_ONLY_SENIOR);
					}
				}

				seatBucketValue.put("status", ReservationStatus.PROCESSING.name());
				seatBucketValue.put("userId", String.valueOf(userId));
				seatBucketValue.put("createdAt", LocalDateTime.now().toString());
				seatBucketValue.put("expiredAt", LocalDateTime.now().plusMinutes(9).toString());

				seatBucketKey.set(seatBucketValue, Duration.ofMinutes(20));

				ToTicketSeatDto seatInfo = parseSeatBucket(seatBucketKey.getName(), seatBucketValue);
				seatInfos.add(seatInfo);

				log.info("좌석: {}, 선점 완료", seatBucketKey.getName());
			}

			ToTicketDto ticketDto = seatApplicationMapper.toTicketDto(matchInfo, seatInfos);

			kafkaTemplate.send("ticket-created", ticketDto);
		} catch (Exception e) {
			throw new GlobalException(SeatErrorCode.FAILED_PROCESS_SEAT);
		} finally {
			for (RLock lock : locks) {
				if (lock.isHeldByCurrentThread()) {
					lock.unlock();
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

		return name != null ? name.toString() : Membership.NORMAL.name();
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
		StringBuilder cacheSeatKey = new StringBuilder()
			.append(cacheSeatKeyFront)
			.append(column)
			.append(":")
			.append(row);

		return cacheSeatKey.toString();
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