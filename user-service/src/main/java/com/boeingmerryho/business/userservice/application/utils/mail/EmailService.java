package com.boeingmerryho.business.userservice.application.utils.mail;

public interface EmailService {

	void sendVerificationEmail(String toEmail, String code);

}
