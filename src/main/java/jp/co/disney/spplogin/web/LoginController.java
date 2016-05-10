package jp.co.disney.spplogin.web;

import java.net.URI;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.disney.spplogin.enums.CoreApiErrors;
import jp.co.disney.spplogin.exception.ApplicationErrors;
import jp.co.disney.spplogin.exception.ApplicationException;
import jp.co.disney.spplogin.service.CoreWebApiService;
import jp.co.disney.spplogin.util.SecureRandomUtil;
import jp.co.disney.spplogin.web.form.EmptyMailForm;
import jp.co.disney.spplogin.web.form.LoginForm;
import jp.co.disney.spplogin.web.model.Guest;
import lombok.extern.slf4j.Slf4j;

/**
 * ログイン／新規登録画面コントローラー
 *
 */
@Slf4j
@Controller
@RequestMapping("/Login")
public class LoginController {

	private final static int SELECT_BARTHDAY_START_YEAR = 1901;
	private final static String DEFAULT_BARTHDAY_YEAR = "1989";
	private final static String DEFAULT_BARTHDAY_MONTH = "1";
	private final static String DEFAULT_BARTHDAY_DAY = "1";
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
	private HttpSession session;

	@Autowired
	private Guest guest;

	@Autowired
	private RedisTemplate<String, Guest> redisTemplate;

	@Autowired
	private CoreWebApiService coreWebApiService;

	/**
	 * 誕生日の年ドロップダウンリストを生成する。
	 * 
	 * @return 年リスト
	 */
	@ModelAttribute(value = "barthdayYears")
	List<Integer> barthdayYears() {
		final int currentYear = Year.now().getValue();
		return IntStream.rangeClosed(SELECT_BARTHDAY_START_YEAR, currentYear).mapToObj(Integer::valueOf)
				.collect(Collectors.toList());
	}

	@ModelAttribute(value = "loginForm")
	LoginForm setUpLoginForm() {
		return new LoginForm();
	}

	@ModelAttribute(value = "emptyMailForm")
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
		return "login/login";
	}

	/**
	 * ログインボタン押下時
	 */
	@RequestMapping(params = "login", method = RequestMethod.POST)
	public Object memberLogin(@ModelAttribute(value = "loginForm") @Valid LoginForm form, BindingResult result,
			RedirectAttributes attributes, Model model, @RequestHeader("User-Agent") String userAgent) {
		if (result.hasErrors()) {
			model.addAttribute("hasErrorForLogin", true);
			return "login/login";
		}

		// 認証認可APIコール
		final ResponseEntity<String> response = coreWebApiService.authorize(form.getMemberNameOrEmailAddr(),
				form.getPassword(), userAgent);
		
		if(response.getStatusCode().series().equals(HttpStatus.Series.CLIENT_ERROR)) {
			 final String apiErrorCode = parseQueryString(response.getHeaders().getLocation(), "error_description");
			 if(apiErrorCode == null) {
				 log.error("認証認可API呼び出しエラー時のエラーコードが未設定です。 Location : {}", response.getHeaders().getLocation().toString());
				 throw new ApplicationException(ApplicationErrors.UNEXPECTED, "ログインエラー判定不能");
			 }
			 
			 if(apiErrorCode.equals(CoreApiErrors.FAILED_OR_INVALID.getCode())
					 || apiErrorCode.equals(CoreApiErrors.UNAUTHORIZED.getCode())) {
				 // ログイン失敗。メンバー名またはパスワード不正
				 model.addAttribute("apiLoginFailed", true);
				 return "login/login";
			 } else {
				 //　アカウント状態不正
				 return "redirect:/OneidStatus";
			 }
		}
		
		return response;
	}

	private String parseQueryString(URI url, String queryKey) {
		String val = null;
		for(NameValuePair p : URLEncodedUtils.parse(url, "UTF-8")){
			if(p.getName().equals(queryKey)) {
				return p.getValue();
			}
		}
		return val;
	}
	
	/**
	 * はじめて利用される方はこちらボタン押下時
	 */
	@RequestMapping(params = "firstTimeOfUse", method = RequestMethod.POST)
	public String firstTimeOfUse(@ModelAttribute(value = "emptyMailForm") @Valid EmptyMailForm form,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("hasErrorForRegister", true);
			return "login/login";
		}

		guest.setBirthDay(form.birthday("/"));

		return "redirect:/Login/emptymail";
	}

	/**
	 * 空メール送信確認ページ表示
	 */
	@RequestMapping(value = "emptymail", method = RequestMethod.GET)
	public String confirmEmptMailForm() {
		return "login/sendEmptyMail";
	}

	/**
	 * 空メール送信先アドレスを返す<br/>
	 * これだけRestController
	 */
	@RequestMapping(value = "emptyMailAddress", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, String>> sendEmptMail() {

		final String coopKey;
		if (session.getAttribute(SESSION_COOP_KEY) == null) {
			coopKey = SecureRandomUtil.genToken();
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

}
