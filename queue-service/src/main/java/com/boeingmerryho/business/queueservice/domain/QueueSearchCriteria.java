package com.boeingmerryho.business.queueservice.domain;

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
	private final Boolean isDeleted;

}
