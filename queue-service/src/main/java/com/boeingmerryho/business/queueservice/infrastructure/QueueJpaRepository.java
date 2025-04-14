package com.boeingmerryho.business.queueservice.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.queueservice.domain.Queue;
import com.boeingmerryho.business.queueservice.domain.repository.QueueRepository;

public interface QueueJpaRepository extends JpaRepository<Queue, Long>, QueueRepository {

}
