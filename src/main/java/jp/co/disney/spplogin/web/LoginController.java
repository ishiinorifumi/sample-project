package jp.co.disney.spplogin.web;

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
@RequestMapping("/spplogin")
public class LoginController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String getLogin() {
		return "login/login";
	}
	
	@RequestMapping(value="emptymail", method = RequestMethod.GET)
	public String sendEmptMail() {
		return "login/sendEmptyMail";
	}
}
