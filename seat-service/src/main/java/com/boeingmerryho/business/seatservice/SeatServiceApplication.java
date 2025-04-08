package com.boeingmerryho.business.seatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient
@SpringBootApplication
public class SeatServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeatServiceApplication.class, args);
	}
}