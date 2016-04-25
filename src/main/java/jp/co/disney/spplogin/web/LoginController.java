package jp.co.disney.spplogin.web;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.disney.spplogin.util.SecureRandomUtil;
import jp.co.disney.spplogin.web.form.EmptyMailForm;
import jp.co.disney.spplogin.web.form.LoginForm;
import lombok.extern.slf4j.Slf4j;

/**
 * ログイン／新規登録画面コントローラー
 *
 */
@Slf4j
@Controller
@RequestMapping("/SPPLogin")
public class LoginController {
	
	private final static int SELECT_BARTHDAY_START_YEAR = 1901;
	private final static String DEFAULT_BARTHDAY_YEAR = "1989";
	private final static String DEFAULT_BARTHDAY_MONTH = "1";
	private final static String DEFAULT_BARTHDAY_DAY = "1";
	
	@Value("${emptymail.domain}")
	private String emptyMailDomain;
	
	@Value("${emptymail.account.prefix}")
	private String accountPrefix;
	
	@Autowired
	private HttpSession session;
	
	/**
	 * 誕生日の年ドロップダウンリストを生成する。
	 * @return 年リスト
	 */
	@ModelAttribute(value="barthdayYears")
	List<Integer> barthdayYears() {
		final int currentYear = Year.now().getValue();
		return IntStream.rangeClosed(SELECT_BARTHDAY_START_YEAR, currentYear)
				.mapToObj(Integer::valueOf)
				.collect(Collectors.toList());
	}
	
	@ModelAttribute(value="loginForm") 
	LoginForm setUpLoginForm() {
		return new LoginForm();
	}
	
	@ModelAttribute(value="emptyMailForm") 
	EmptyMailForm setUpEmptyMailForm() {
		EmptyMailForm form = new EmptyMailForm();
		form.setBirthdayYear(DEFAULT_BARTHDAY_YEAR);
		form.setBirthdayMonth(DEFAULT_BARTHDAY_MONTH);
		form.setBirthdayDay(DEFAULT_BARTHDAY_DAY);
		return form;
	}
	
	/**
	 * ログイン／新規登録ページ表示
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String loginOrRegistForm(Model model) {
		//throw new RuntimeException();
		return "login/login";
	}
	
	/**
	 * ログインボタン押下時
	 */
	@RequestMapping(params = "login", method = RequestMethod.POST)
	public String memberLogin(@ModelAttribute(value="loginForm") @Valid LoginForm form, BindingResult result, RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
        	model.addAttribute("hasError", true);
            return "login/login";
        }
        return "redirect:/SPPLogin/emptymail";
	}

	/**
	 * はじめて利用される方はこちらボタン押下時
	 */
	@RequestMapping(params = "firstTimeOfUse", method = RequestMethod.POST)
	public String firstTimeOfUse(@ModelAttribute(value="emptyMailForm") @Valid EmptyMailForm form, BindingResult result, RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
        	model.addAttribute("hasError", true);
            return "login/login";
        }
        session.setAttribute("birthday", form.birthday("/"));
        return "redirect:/SPPLogin/emptymail";
	}
	
	/**
	 * 空メール送信確認ページ表示
	 */
	@RequestMapping(value="emptymail", method = RequestMethod.GET)
	public String confirmEmptMailForm() {
		return "login/sendEmptyMail";
	}
	

	/**
	 * 空メール送信先アドレスを返す<br/>
	 * これだけRestController
	 */
	@RequestMapping(value="sendEmptMail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, String>> sendEmptMail() {
		Map<String, String> json = new HashMap<>();
		String key = SecureRandomUtil.genToken();
		json.put("to-address", accountPrefix + key + "@" + emptyMailDomain);
		return new ResponseEntity<Map<String, String>>(json, HttpStatus.OK);
	}
	
}
 