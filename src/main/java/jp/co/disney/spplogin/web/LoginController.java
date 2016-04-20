package jp.co.disney.spplogin.web;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		return  new LoginForm();
	}
	
	@ModelAttribute(value="emptyMailForm") 
	EmptyMailForm setUpEmptyMailForm() {
		EmptyMailForm form = new EmptyMailForm();
		form.setBirthdayYear(DEFAULT_BARTHDAY_YEAR);
		form.setBirthdayMonth(DEFAULT_BARTHDAY_MONTH);
		form.setBirthdayDay(DEFAULT_BARTHDAY_DAY);
		return form;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String loginOrRegist(Model model) {
		return "login/login";
	}
	
	@RequestMapping(params = "login", method = RequestMethod.POST)
	public String memberLogin(@ModelAttribute(value="loginForm") @Valid LoginForm form, BindingResult result, RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
            for(FieldError err: result.getFieldErrors()) {
                log.debug("error code = [" + err.getCode() + "]");
            }
            return "login/login";
            
        }

        return "redirect:/SPPLogin/emptymail";
	}

	@RequestMapping(params = "firstTimeOfUse", method = RequestMethod.POST)
	public String firstTimeOfUse(@ModelAttribute(value="emptyMailForm") @Valid EmptyMailForm form, BindingResult result, RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
            for(FieldError err: result.getFieldErrors()) {
                log.debug("error code = [" + err.getCode() + "]");
            }
            return "login/login";
            
        }

        return "redirect:/SPPLogin/emptymail";
	}
	
	@RequestMapping(value="emptymail", method = RequestMethod.GET)
	public String sendEmptMail() {
		return "login/sendEmptyMail";
	}
}
