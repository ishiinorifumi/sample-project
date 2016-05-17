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

	@Value("${spplogin.emptymail.session-coop-key.expire}")
	private int coopKeyExpire;

	@Value("${spplogin.emptymail.session-coop-key.expire-timeunit}")
	private String coopKeyExpireTimeUnit;
	
	@Autowired
	private Guest guest;
	
	@Autowired
	private RedisTemplate<String, Guest> redisTemplate;
	
	/**
	 * 空メール送信先アドレスを返す<br/>
	 * これだけRestController
	 */
	@RequestMapping(value = "genToAddress", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> sendEmptMail(HttpSession session) {

		final String coopKey;
		if (session.getAttribute(SESSION_COOP_KEY) == null) {
			coopKey = UUID.randomUUID().toString();
			session.setAttribute(SESSION_COOP_KEY, coopKey);
		} else {
			coopKey = (String) session.getAttribute(SESSION_COOP_KEY);
		}

		redisTemplate.opsForValue().set(coopKey, guest.copy());
		redisTemplate.expire(coopKey, coopKeyExpire, TimeUnit.valueOf(coopKeyExpireTimeUnit));

		Map<String, String> res = new HashMap<>();
		res.put("to_address", accountPrefix + coopKey + "@" + emptyMailDomain);
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value = "autoReply", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> autoReply(@RequestBody Map<String, String> req) {
		log.info("空メールの自動応答メール送信処理を開始します。");
		final String sessionCoopId = req.get("session_coop_id");
		final String emailAddress = req.get("email_address");
		
		final Map<String, String> res = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		if(!StringUtils.isEmpty(sessionCoopId) && !StringUtils.isEmpty(emailAddress)) {
			final Guest guest = redisTemplate.opsForValue().get(sessionCoopId);
			if(guest == null) {
				log.warn("セッション復元用IDからのRedis情報取得に失敗しました。 セッション復元用ID:{}", sessionCoopId);
				res.put("status", "NG");
				res.put("message", "invalid session_coop_id.");
				status = HttpStatus.BAD_REQUEST;
			}
			guest.setMailAddress(emailAddress);
			sendAutoReplyMail(emailAddress, sessionCoopId);
			res.put("status", "OK");
			status = HttpStatus.OK;
		} else {
			log.warn("セッション復元用IDもしくはメールアドレスが未指定です。");
			res.put("status", "NG");
			res.put("message", "email_address and session_coop_id is required.");
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Map<String, String>>(res, status);
	}
	
	/**
	 * 自動返信メール送信
	 * @param emailAddress 宛先アドレス
	 * @param sessionCoopId セッション復元用ID
	 */
	private void sendAutoReplyMail(String emailAddress, String sessionCoopId) {
		log.debug("宛先アドレス : {}", emailAddress);
		log.debug("セッション復元用ID : {}", sessionCoopId);
	}
}
