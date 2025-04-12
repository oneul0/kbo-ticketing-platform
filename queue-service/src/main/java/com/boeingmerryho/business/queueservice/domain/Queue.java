package com.boeingmerryho.business.queueservice.domain;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import io.github.boeingmerryho.commonlibrary.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_queue")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Queue extends BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "store_id", nullable = false)
	private Long storeId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Integer sequence;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private QueueStatus status = QueueStatus.PENDING;

	public static Queue withDefaultStatus(Long storeId, Long userId, Integer sequence) {
		return Queue.builder()
			.storeId(storeId)
			.userId(userId)
			.sequence(sequence)
			.status(QueueStatus.PENDING)
			.build();
	}

}
