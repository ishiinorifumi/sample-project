package jp.co.disney.spplogin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ログイン／新規登録画面コントローラー
 * @author z2h7273
 *
 */
@Controller
public class LoginController {

	@RequestMapping(value = "/spplogin", method = RequestMethod.GET)
	public String getLogin() {
		return "login/login";
	}

}
