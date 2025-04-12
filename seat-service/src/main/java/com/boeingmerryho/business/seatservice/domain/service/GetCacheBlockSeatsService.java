package com.boeingmerryho.business.seatservice.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheBlockServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheSeatServiceResponseDto;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCacheBlockSeatsService {
	private final RedissonClient redissonClient;
	private final SeatApplicationMapper seatApplicationMapper;

	public RList<String> getBlocks(CacheBlockServiceRequestDto request) {
		String cacheBlockKey = createCacheBlockKey(request.blockId(), request.date());
		RList<String> blockSeats = redissonClient.getList(cacheBlockKey);
		if (!blockSeats.isExists()) {
			throw new GlobalException(SeatErrorCode.NOT_FOUND_BLOCK);
		}

		return blockSeats;
	}

	public List<CacheSeatServiceResponseDto> getBlockSeats(RList<String> blockSeats) {
		List<CacheSeatServiceResponseDto> seats = new ArrayList<>();

		for (String blockSeat : blockSeats) {
			RBucket<Map<String, String>> seatBucketKey = redissonClient.getBucket(blockSeat);
			Map<String, String> seatBucketValue = seatBucketKey.get();

			if (seatBucketValue == null) {
				throw new GlobalException(SeatErrorCode.NOT_FOUND_SEAT);
			}

			String status = seatBucketValue.get("status");

			CacheSeatServiceResponseDto seat = seatApplicationMapper.toCacheSeatServiceResponseDto(
				blockSeat,
				status
			);

			seats.add(seat);
		}

		return seats;
	}

	private String createCacheBlockKey(Integer blockId, LocalDate date) {
		String seatPrefix = "seat:";

		StringBuilder cacheBlockKey = new StringBuilder()
			.append(seatPrefix)
			.append(date)
			.append(":")
			.append(blockId);

		return cacheBlockKey.toString();
	}
}