package com.boeingmerryho.business.seatservice.infrastructure.reader;

import java.util.Map;

public interface MatchReader {
	Map<String, Object> getByMatchId(Long matchId);
}