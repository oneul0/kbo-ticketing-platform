package com.boeingmerryho.business.userservice.application.messaging;

import org.springframework.stereotype.Service;

import com.oringmaryho.business.userservice.application.dto.request.SlackMessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageService {

	private final MessagePublisher messagePublisher;

	public void sendSlackMessage(Long id, String message) {
		SlackMessageDto dto = new SlackMessageDto(id, message);
		messagePublisher.publishSlackMessage(dto);
	}
}