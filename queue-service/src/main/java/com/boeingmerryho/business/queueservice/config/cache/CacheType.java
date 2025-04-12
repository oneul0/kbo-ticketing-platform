package com.boeingmerryho.business.queueservice.config.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
	QUEUE("queue", 300, 500),
	STOREQUEUE("storeQueue", 300, 500);

	private final String cacheName;
	private final int expiredAfterWrite;
	private final int maximumSize;
}