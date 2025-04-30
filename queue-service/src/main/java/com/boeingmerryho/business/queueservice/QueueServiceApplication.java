package com.boeingmerryho.business.queueservice;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import jakarta.annotation.PostConstruct;

@EnableDiscoveryClient
@SpringBootApplication
public class QueueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueServiceApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}