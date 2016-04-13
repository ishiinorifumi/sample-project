package jp.co.disney.spplogin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * ログイン／新規登録画面コントローラー
 *
 */
@Slf4j
@Controller
public class LoginController {

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@RequestMapping(value = "/spplogin", method = RequestMethod.GET)
	public String getLogin() {
		return "login/login";
	}
}
