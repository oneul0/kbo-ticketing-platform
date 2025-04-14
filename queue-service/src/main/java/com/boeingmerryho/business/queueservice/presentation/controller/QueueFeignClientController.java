package com.boeingmerryho.business.queueservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.queueservice.application.dto.request.feign.IssuedTicketDto;
import com.boeingmerryho.business.queueservice.application.service.QueueFeignService;
import com.boeingmerryho.business.queueservice.presentation.dto.mapper.QueuePresentationMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/queue-service")
public class QueueFeignClientController {
	private final QueueFeignService queueFeignService;
	private final QueuePresentationMapper mapper;

	@Description("FeignClient - ticket으로부터 받아 저장하는 api")
	@PostMapping("/ticket")
	public ResponseEntity<?> userFeignServiceGetByRole(
		@RequestBody IssuedTicketDto dto
	) {
		queueFeignService.cacheIssuedTicket(dto);
		return ResponseEntity.ok().build();
	}

}
