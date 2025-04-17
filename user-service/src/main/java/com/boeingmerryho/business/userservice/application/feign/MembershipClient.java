package com.boeingmerryho.business.userservice.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.boeingmerryho.business.userservice.application.dto.request.feign.LoginSuccessRequest;

@FeignClient(name = "membership-service")
public interface MembershipClient {

	@PostMapping("membership-service/memberships/users/login")
	ResponseEntity<String> notifyLogin(@RequestBody LoginSuccessRequest request);

}
