package com.boeingmerryho.business.storeservice.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.boeingmerryho.business.storeservice.application.dto.request.StoreUpdateRequestServiceDto;
import com.boeingmerryho.business.storeservice.exception.StoreErrorCode;

import io.github.boeingmerryho.commonlibrary.entity.BaseEntity;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
		nullable = false
	)
	private Long stadiumId;

	@Column(
		nullable = false,
		length = 50
	)
	private String name;

	@Column(
		nullable = false
	)
	private LocalDateTime openAt;

	@Column(
		nullable = false
	)
	private LocalDateTime closedAt;

	@Column(
		nullable = false
	)
	private Boolean isClosed;

	public void update(StoreUpdateRequestServiceDto update) {
		if (update.name() != null)
			this.name = update.name();
		if (update.openAt() != null)
			this.openAt = update.openAt();
		if (update.closedAt() != null)
			this.closedAt = update.closedAt();
	}

	public void open() {
		if (Boolean.FALSE.equals(isClosed)) {
			throw new GlobalException(StoreErrorCode.ALREADY_OPENED);
		}
		this.isClosed = false;
	}

	public void close() {
		if (Boolean.TRUE.equals(isClosed)) {
			throw new GlobalException(StoreErrorCode.ALREADY_CLOSED);
		}
		this.isClosed = true;
	}
}
