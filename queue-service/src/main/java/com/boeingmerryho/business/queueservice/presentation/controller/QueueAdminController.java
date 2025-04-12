package com.boeingmerryho.business.queueservice.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.queueservice.config.pageable.PageableConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/queues")
public class QueueAdminController {

	private final PageableConfig pageableConfig;

}
