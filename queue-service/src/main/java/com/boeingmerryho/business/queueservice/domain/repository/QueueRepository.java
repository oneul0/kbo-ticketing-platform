package com.boeingmerryho.business.queueservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.queueservice.domain.Queue;

public interface QueueRepository extends JpaRepository<Queue, Long> {
}
