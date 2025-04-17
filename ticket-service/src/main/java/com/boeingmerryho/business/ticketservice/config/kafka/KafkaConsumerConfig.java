package com.boeingmerryho.business.ticketservice.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.PaymentListenerDto;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatListenerDto;

@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ConsumerFactory<String, SeatListenerDto> seatConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "ticket-seat");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		return new DefaultKafkaConsumerFactory<>(
			props,
			new StringDeserializer(),
			new JsonDeserializer<>(SeatListenerDto.class, false)
		);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SeatListenerDto> seatKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, SeatListenerDto> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(seatConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, PaymentListenerDto> paymentConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "ticket-payment");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		return new DefaultKafkaConsumerFactory<>(
			props,
			new StringDeserializer(),
			new JsonDeserializer<>(PaymentListenerDto.class, false)
		);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PaymentListenerDto> paymentKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, PaymentListenerDto> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(paymentConsumerFactory());
		return factory;
	}
}
