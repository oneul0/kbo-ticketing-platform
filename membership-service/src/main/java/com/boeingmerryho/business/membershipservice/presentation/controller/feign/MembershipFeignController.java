package com.boeingmerryho.business.membershipservice.presentation.controller.feign;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.membershipservice.application.dto.request.LoginSuccessRequest;
import com.boeingmerryho.business.membershipservice.application.service.feign.MembershipFeignService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MembershipFeignController {

	private final MembershipFeignService membershipFeignService;

	@PostMapping("/membership/user/login")
	public ResponseEntity<String> notifyLogin(@RequestBody LoginSuccessRequest request) {
		membershipFeignService.handleLoginSuccess(request);
		return ResponseEntity.ok("로그인 성공 처리 완료");
	}
}
