package com.boeingmerryho.business.seatservice.domain.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatsProcessServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketMatchDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketSeatDto;
import com.boeingmerryho.business.seatservice.domain.ReservationStatus;
import com.boeingmerryho.business.seatservice.exception.MatchErrorCode;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;
import com.boeingmerryho.business.seatservice.infrastructure.helper.MatchHelper;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessBlockSeatsService {
	private final MatchHelper matchHelper;
	private final RedissonClient redissonClient;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final SeatApplicationMapper seatApplicationMapper;

	public ToTicketMatchDto getMatchInfo(CacheSeatsProcessServiceRequestDto serviceDto) {
		Map<String, Object> match = matchHelper.getByMatchId(serviceDto.matchId());

		if (!Objects.equals(match.get("match_day").toString(), serviceDto.date().toString())) {
			log.error("Request Date : {} - Match Date : {}", serviceDto.date(), match.get("match_day"));
			throw new GlobalException(MatchErrorCode.NOT_MATCH_DATE);
		}

		return new ToTicketMatchDto(
			match.get("id").toString(),
			match.get("home_team_id").toString(),
			match.get("away_team_id").toString(),
			match.get("match_day").toString(),
			match.get("stadium_id").toString()
		);
	}

	public void getBlockSeats(
		List<RLock> locks,
		String cacheBlockKey,
		RList<String> blockSeats,
		List<String> requestSeats,
		List<CacheSeatProcessServiceRequestDto> serviceRequestSeatInfos
	) {
		for (CacheSeatProcessServiceRequestDto requestSeat : serviceRequestSeatInfos) {
			String cacheSeatKey = createCacheSeatKey(
				cacheBlockKey,
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

	public void processBlockSeats(
		ToTicketMatchDto matchInfo,
		List<RLock> locks,
		List<String> requestSeats,
		List<ToTicketSeatDto> seatInfos
	) {
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
					throw new GlobalException(SeatErrorCode.FAILED_PROCESS_SEAT);
				}

				seatBucketValue.put("status", ReservationStatus.PROCESSING.name());
				seatBucketValue.put("userId", "1");
				seatBucketValue.put("createdAt", LocalDateTime.now().toString());
				seatBucketValue.put("expiredAt", LocalDateTime.now().plusMinutes(9).toString());

				seatBucketKey.set(seatBucketValue, Duration.ofMinutes(5));

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

	public RList<String> getBlockSeats(String cacheBlockKey) {
		RList<String> blockSeats = redissonClient.getList(cacheBlockKey);
		if (!blockSeats.isExists()) {
			throw new GlobalException(SeatErrorCode.NOT_FOUND_BLOCK);
		}

		return blockSeats;
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

	private String createLockKey(String cacheSeatKey) {
		StringBuilder cacheLockKey = new StringBuilder()
			.append(cacheSeatKey)
			.append(":lock");

		return cacheLockKey.toString();
	}

	private String createCacheSeatKey(String cacheBlockKey, Integer column, Integer row) {
		StringBuilder cacheSeatKey = new StringBuilder()
			.append(cacheBlockKey)
			.append(":")
			.append(column)
			.append(":")
			.append(row);

		return cacheSeatKey.toString();
	}
}