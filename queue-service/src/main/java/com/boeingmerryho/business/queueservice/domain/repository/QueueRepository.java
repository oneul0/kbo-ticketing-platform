package com.boeingmerryho.business.queueservice.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.queueservice.domain.entity.Queue;

public interface QueueRepository extends JpaRepository<Queue, Long> {
	Optional<Queue> findAllById(Long id);
}
