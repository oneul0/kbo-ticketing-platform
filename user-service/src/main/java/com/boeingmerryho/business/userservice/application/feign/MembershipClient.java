package com.boeingmerryho.business.userservice.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.boeingmerryho.business.userservice.application.dto.request.feign.LoginSuccessRequest;

@FeignClient(name = "membership-service")
public interface MembershipClient {

	@PostMapping("/membership/user/login")
	ResponseEntity<String> notifyLogin(@RequestBody LoginSuccessRequest request);

}
