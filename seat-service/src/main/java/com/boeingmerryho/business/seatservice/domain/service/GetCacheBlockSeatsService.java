package com.boeingmerryho.business.seatservice.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.redisson.api.RBatch;
import org.redisson.api.RFuture;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheBlockServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheSeatServiceResponseDto;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatCommonHelper;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCacheBlockSeatsService {
	private final RedissonClient redissonClient;
	private final SeatCommonHelper seatCommonHelper;
	private final SeatApplicationMapper seatApplicationMapper;

	public RSet<String> getBlocks(CacheBlockServiceRequestDto request) {
		String cacheBlockKey = seatCommonHelper.createCacheBlockKey(request.blockId(), request.date());
		RSet<String> blockSeats = redissonClient.getSet(cacheBlockKey);
		if (!blockSeats.isExists()) {
			throw new GlobalException(SeatErrorCode.NOT_FOUND_BLOCK);
		}

		return blockSeats;
	}

	public List<CacheSeatServiceResponseDto> getBlockSeats(RSet<String> blockSeats) {
		List<CacheSeatServiceResponseDto> seats = new ArrayList<>();

		RBatch batch = redissonClient.createBatch();
		Map<String, RFuture<Map<String, String>>> futureMap = new HashMap<>();

		for (String blockSeat : blockSeats) {
			RFuture<Map<String, String>> future = batch.<Map<String, String>>getBucket(blockSeat).getAsync();
			futureMap.put(blockSeat, future);
		}

		batch.execute();

		for (Map.Entry<String, RFuture<Map<String, String>>> entry : futureMap.entrySet()) {
			String blockSeat = entry.getKey();
			Map<String, String> seatBucketValue;
			try {
				seatBucketValue = entry.getValue().get();
			} catch (Exception e) {
				throw new GlobalException(SeatErrorCode.NOT_FOUND_BLOCK);
			}

			if (seatBucketValue == null) {
				throw new GlobalException(SeatErrorCode.NOT_FOUND_SEAT);
			}

			String status = seatBucketValue.get("status");
			CacheSeatServiceResponseDto seat = seatApplicationMapper.toCacheSeatServiceResponseDto(blockSeat, status);

			seats.add(seat);
		}

		return seats;
	}
}