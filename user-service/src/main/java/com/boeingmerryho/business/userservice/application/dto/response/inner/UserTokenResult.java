package com.boeingmerryho.business.userservice.application.dto.response.inner;

import java.util.Map;

public record UserTokenResult(String tokenKey, Map<Object, Object> token) {
}
