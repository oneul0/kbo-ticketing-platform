package com.boeingmerryho.business.storeservice.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreQueueAdminHelper;
import com.boeingmerryho.business.storeservice.infrastructure.kafka.scheduler.message.StoreQueueScheduleMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreQueueKafkaConsumer {

	private final ObjectMapper objectMapper;
	private final StoreQueueAdminHelper storeQueueAdminHelper;

	@KafkaListener(topics = "${store.queue.schedule.topic}", groupId = "store-queue-scheduler-group")
	public void listen(String message) {
		try {
			StoreQueueScheduleMessage parsed = objectMapper.readValue(message, StoreQueueScheduleMessage.class);
			log.info("Received queue schedule message: {}", parsed);

			if (parsed.enable()) {
				storeQueueAdminHelper.enableQueue(parsed.storeId());
				log.info("Queue enabled for storeId={}", parsed.storeId());
			} else {
				storeQueueAdminHelper.disableQueue(parsed.storeId());
				log.info("Queue disabled for storeId={}", parsed.storeId());
			}
		} catch (Exception e) {
			log.error("Failed to process message: {}", message, e);
		}
	}
}
