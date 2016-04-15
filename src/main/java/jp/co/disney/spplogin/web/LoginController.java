package jp.co.disney.spplogin.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * ログイン／新規登録画面コントローラー
 *
 */
@Slf4j
@Controller
@RequestMapping("/SPPLogin")
public class LoginController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("barthdayYears", barthdayYears());
		return "login/login";
	}
	
	@RequestMapping(value="emptymail", method = RequestMethod.GET)
	public String sendEmptMail() {
		return "login/sendEmptyMail";
	}
	
	private List<Integer> barthdayYears() {
		final List<Integer> years = new ArrayList<>();
		years.add(1901);
		years.add(1902);
		years.add(1903);
		return years;
	}
}
