package jp.co.disney.spplogin.service;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import jp.co.disney.spplogin.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("unit")
public class MailServiceTest {
	
	@Rule
    public final MockitoRule rule = MockitoJUnit.rule();
	
	@InjectMocks
	@Autowired
	private MailService mailService;
	
	@Mock
	private MailSender mailSender;
	
	@Test
	public void sendMemberRegisterMailのテスト() throws Exception {
		doNothing().when(mailSender).send(any(SimpleMailMessage.class));;
		mailService.sendMemberRegisterMail("seiji.takahashi.090@ctc-g.co.jp", "a1-af4f8266-cc13-4caa-b9e4-3df8c893e0d0");
	}
}
