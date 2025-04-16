package com.boeingmerryho.business.queueservice.domain.entity;

import java.util.Date;

import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.domain.model.QueueStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QueueSearchCriteria {
	private final Long id;
	private final Long storeId;
	private final Long userId;
	private final Integer sequence;
	private final QueueStatus status;
	private final CancelReason cancelReason;
	private final Date startDate;
	private final Date endDate;
	private final Boolean isDeleted;

}
