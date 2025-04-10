package com.boeingmerryho.business.storeservice.infrastructure.reader;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.storeservice.domain.reader.StadiumReader;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StadiumReaderImpl implements StadiumReader {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public boolean existsById(Long stadiumId) {
		String sql = "SELECT EXISTS (SELECT 1 FROM p_stadium WHERE id = ?)";
		Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, stadiumId);
		return Boolean.TRUE.equals(exists);
	}
}
