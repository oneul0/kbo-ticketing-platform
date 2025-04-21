package com.boeingmerryho.business.seatservice.infrastructure.reader;

import java.time.LocalDate;
import java.util.Map;

public interface MatchReader {
	Map<String, Object> getByMatchId(Long matchId);

	Map<String, Object> getByMatchDate(LocalDate matchDate);
}