package jp.co.disney.spplogin.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.disney.spplogin.helper.RandomHelper;
import jp.co.disney.spplogin.service.MailService;
import jp.co.disney.spplogin.web.model.Guest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/EmptyMail")
public class EmptyMailController {

	private final String SESSION_COOP_KEY = "spplogin.session-coop-key";
	
	@Value("${spplogin.emptymail.domain}")
	private String emptyMailDomain;

	@Value("${spplogin.emptymail.account-prefix}")
	private String accountPrefix;
	
	@Value("${spplogin.emptymail.account-separator}")
	private String accountSeparator;

	@Value("${spplogin.emptymail.session-coop-key.prefix}")
	private String coopKeyPrefix;
	
	@Value("${spplogin.emptymail.session-coop-key.expire}")
	private int coopKeyExpire;

	@Value("${spplogin.emptymail.session-coop-key.expire-timeunit}")
	private String coopKeyExpireTimeUnit;
	
	@Autowired
	private Guest guest;
	
	@Autowired
	private RedisTemplate<String, Guest> redisTemplate;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private RandomHelper randomHelper;
	
	/**
	 * 空メール送信先アドレスを返す
	 */
	@RequestMapping(value = "genToAddress", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> sendEmptMail(HttpSession session) {

		String coopKey;
		if (session.getAttribute(SESSION_COOP_KEY) == null) {
			coopKey = coopKeyPrefix + randomHelper.randomID();
			session.setAttribute(SESSION_COOP_KEY, coopKey);
			redisTemplate.opsForValue().set(coopKey, guest.copy());
		} else {
			coopKey = (String) session.getAttribute(SESSION_COOP_KEY);
		}

		Map<String, String> res = new HashMap<>();
		res.put("to_address", accountPrefix + accountSeparator + coopKey + "@" + emptyMailDomain);
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value = "AutoReply", method = RequestMethod.POST, produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<String> autoReply(
			@RequestParam(value = "from", required = true) final String emailAddress,
			@RequestParam(required = true) final String to) {
		log.info("空メールの自動応答メール送信処理を開始します。");

		log.debug("From : {}", emailAddress);
		log.debug("To : {}", to);
		
		String sessionCoopId = null;
		
		final String toAccount = to.split("@")[0];
		
		final String[] accountInfo = toAccount.split(accountSeparator);
		if(accountInfo.length == 2) {
			sessionCoopId = accountInfo[1];
		}
		
		final Map<String, String> res = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		if(!StringUtils.isEmpty(sessionCoopId) && !StringUtils.isEmpty(emailAddress)) {
			
			log.debug("sessionCooopId : {}", sessionCoopId);
			
			final Guest guest = redisTemplate.opsForValue().get(sessionCoopId);
			if(guest == null) {
				log.warn("セッション復元用IDによるRedisからの情報取得に失敗しました。 : {}", sessionCoopId);
				res.put("status", "NG");
				res.put("message", "invalid session_coop_id.");
				status = HttpStatus.BAD_REQUEST;
			} else {
				guest.setMailAddress(emailAddress);
				redisTemplate.opsForValue().set(sessionCoopId, guest);
				redisTemplate.expire(sessionCoopId, coopKeyExpire, TimeUnit.valueOf(coopKeyExpireTimeUnit));
				mailService.sendMemberRegisterMail(emailAddress, sessionCoopId);
				res.put("status", "OK");
				status = HttpStatus.OK;
			}
		} else {
			log.warn("セッション復元用IDもしくはメールアドレスが未指定です。 : {}", to);
			res.put("status", "NG");
			res.put("message", "email_address and session_coop_id is required.");
			status = HttpStatus.BAD_REQUEST;
		}
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	
		try {
			return new ResponseEntity<>(new ObjectMapper().writeValueAsString(res), headers, status);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
