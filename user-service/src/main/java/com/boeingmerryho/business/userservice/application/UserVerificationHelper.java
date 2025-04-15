package com.boeingmerryho.business.userservice.application;

public interface UserVerificationHelper {

	String generateVerificationCode();

	void storeVerificationCode(String email, String code);

	String getVerificationCode(String email);

	void removeVerificationCode(String email);

	void checkDuplicatedVerificationRequest(String email);

	String getNotifyLoginResponse(Long id);

}

