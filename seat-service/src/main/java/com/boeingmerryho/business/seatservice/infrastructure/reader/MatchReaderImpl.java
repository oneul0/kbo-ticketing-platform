package com.boeingmerryho.business.seatservice.infrastructure.reader;

import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.seatservice.exception.MatchErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchReaderImpl implements MatchReader {
	private final JdbcTemplate jdbcTemplate;

	@Override
	public Map<String, Object> getByMatchId(Long matchId) {
		try {
			String sql = getByMatchIdQuery();
			return jdbcTemplate.queryForMap(sql, matchId);
		} catch (EmptyResultDataAccessException e) {
			throw new GlobalException(MatchErrorCode.NOT_FOUND_MATCH);
		}
	}

	private String getByMatchIdQuery() {
		return "SELECT * FROM p_match WHERE id = ?";
	}
}