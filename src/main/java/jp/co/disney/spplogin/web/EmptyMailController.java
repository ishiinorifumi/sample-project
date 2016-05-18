package jp.co.disney.spplogin.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
	
	/**
	 * 空メール送信先アドレスを返す<br/>
	 * これだけRestController
	 */
	@RequestMapping(value = "genToAddress", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> sendEmptMail(HttpSession session) {

		String coopKey;
		if (session.getAttribute(SESSION_COOP_KEY) == null) {
			coopKey = coopKeyPrefix + UUID.randomUUID().toString();
			session.setAttribute(SESSION_COOP_KEY, coopKey);
			redisTemplate.opsForValue().set(coopKey, guest.copy());
			redisTemplate.expire(coopKey, coopKeyExpire, TimeUnit.valueOf(coopKeyExpireTimeUnit));
		} else {
			coopKey = (String) session.getAttribute(SESSION_COOP_KEY);
		}

		Map<String, String> res = new HashMap<>();
		res.put("to_address", accountPrefix + accountSeparator + coopKey + "@" + emptyMailDomain);
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value = "autoReply", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> autoReply(@RequestBody Map<String, String> req) {
		log.info("空メールの自動応答メール送信処理を開始します。");
		log.debug("From : {}", req.get("from"));
		log.debug("To : {}", req.get("to"));
		
		final String emailAddress = req.get("from");
		String sessionCoopId = null;
		
		final String to = req.get("to");
		final String[] accountInfo = to.split(accountSeparator);
		if(accountInfo.length == 2) {
			sessionCoopId = accountInfo[1];
		}
		
		final Map<String, String> res = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		if(!StringUtils.isEmpty(sessionCoopId) && !StringUtils.isEmpty(emailAddress)) {
			final Guest guest = redisTemplate.opsForValue().get(sessionCoopId);
			if(guest == null) {
				log.warn("セッション復元用IDによるRedisからの情報取得に失敗しました。 : {}", to);
				res.put("status", "NG");
				res.put("message", "invalid session_coop_id.");
				status = HttpStatus.BAD_REQUEST;
			}
			guest.setMailAddress(emailAddress);
			mailService.sendMemberRegisterMail(emailAddress, sessionCoopId);
			
			res.put("status", "OK");
			status = HttpStatus.OK;
		} else {
			log.warn("セッション復元用IDもしくはメールアドレスが未指定です。 : {}", to);
			res.put("status", "NG");
			res.put("message", "email_address and session_coop_id is required.");
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Map<String, String>>(res, status);
	}
}
