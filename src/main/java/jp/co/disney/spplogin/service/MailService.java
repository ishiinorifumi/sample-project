package jp.co.disney.spplogin.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailService {
    
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@Value("${spplogin.base-url}")
	private String baseUrl;
	@Value("${spplogin.emptymail.auto-reply.from-address}")
	private String autoReplyFromAddress;
	@Value("${spplogin.emptymail.auto-reply.subject}")
	private String autoReplySubject;
	
	/**
	 * SPPメンバ―登録用URLを記載したメールを送信する。
	 * @param toAddress 宛先アドレス
	 * @param sessionCoopId セッション復元用ID
	 */
	public void sendMemberRegisterMail(String toAddress, String sessionCoopId) {
		log.debug("SPPメンバー登録メール宛先アドレス : {}", toAddress);
		log.debug("セッション復元用ID : {}", sessionCoopId);
		
		final Map<String, Object> model = new HashMap<>();
		model.put("memberRegisterUrl", baseUrl + "Regist?form=" + sessionCoopId);
		final String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/member-register.vm", "UTF-8", model);
		
		log.debug("SPPメンバー登録メールBody : {}", body);
		
		final SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toAddress);
		message.setFrom(autoReplyFromAddress);
		message.setSubject(autoReplySubject);
		message.setText(body);
		
		mailSender.send(message);
	}
}
