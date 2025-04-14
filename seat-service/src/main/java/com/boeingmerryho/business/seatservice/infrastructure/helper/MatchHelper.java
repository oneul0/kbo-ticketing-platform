package com.boeingmerryho.business.seatservice.infrastructure.helper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.seatservice.infrastructure.reader.MatchReaderImpl;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchHelper {
	private final MatchReaderImpl matchReaderImpl;

	public Map<String, Object> getByMatchId(Long matchId) {
		return matchReaderImpl.getByMatchId(matchId);
	}
}