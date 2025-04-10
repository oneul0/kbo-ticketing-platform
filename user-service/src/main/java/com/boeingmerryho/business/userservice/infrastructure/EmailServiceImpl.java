package com.boeingmerryho.business.userservice.infrastructure;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.userservice.application.utils.mail.EmailService;
import com.boeingmerryho.business.userservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	@Override
	public void sendVerificationEmail(String toEmail, String code) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toEmail);
			message.setSubject("보잉 메리호 Email 인증 코드");
			message.setText("인증 코드 : " + code + "\n이 인증 코드는 5분 동안 유효합니다.");
			log.info("receiver email address : {}", toEmail);
			mailSender.send(message);
			log.info("Email sent successfully to: {}", toEmail);
		} catch (MailException e) {
			log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
			throw new GlobalException(ErrorCode.EMAIL_SEND_FAILED);
		}
	}
	// public void sendVerificationEmail(String toEmail, String authCode) {
	// 	log.info("메일 발송 시작 -> 수신자: {}, 인증번호: {}", toEmail, authCode);
	// 	MimeMessage mimeMessage = mailSender.createMimeMessage();
	//
	// 	try {
	// 		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
	//
	// 		mimeMessageHelper.setTo(toEmail);
	// 		mimeMessageHelper.setSubject("안녕하세요 인증번호 안내입니다.");
	//
	// 		String content = String.format("""
	// 				<!DOCTYPE html>
	// 				<html xmlns:th="http://www.thymeleaf.org">
	//
	// 				<body>
	// 				<div style="margin:100px;">
	// 				    <h1> 인증번호 안내입니다. </h1>
	// 				    <br>
	//
	//
	// 				    <div align="center" style="border:1px solid black;">
	// 				        <h3> 인증번호는 <b>%s</b> 입니다. </h3>
	// 				    </div>
	// 				    <br/>
	// 				</div>
	//
	// 				</body>
	// 				</html>
	// 				""",
	// 			authCode
	// 		);
	//
	// 		mimeMessageHelper.setText(content, true);
	//
	// 		mailSender.send(mimeMessage);
	//
	// 		log.info("메일 발송 성공!");
	// 	} catch (Exception e) {
	// 		log.error("메일 발송 실패! 에러 메시지: {}", e.getMessage(), e);
	// 	}
	// }
}
