package jp.co.disney.spplogin.web;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.disney.spplogin.enums.CoreApiErrors;
import jp.co.disney.spplogin.exception.ApplicationErrors;
import jp.co.disney.spplogin.exception.ApplicationException;
import jp.co.disney.spplogin.helper.URLDecodeHelper;
import jp.co.disney.spplogin.service.CoreWebApiService;
import jp.co.disney.spplogin.vo.DidMemberDetails;
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

	@Autowired
	private Guest guest;

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
	public String loginOrRegistForm(@RequestParam(required = true) String dspp,
			@RequestParam(value="service_name", required = true) String serviceName, Model model) {
		guest.setDspp(dspp);
		
		try {
			guest.setServiceName(URLDecoder.decode(serviceName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		log.debug("dspp: {}", dspp);
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
				form.getPassword(), userAgent, guest.getDspp());
		
		final URI redirectURL =  response.getHeaders().getLocation();
		
		if(response.getStatusCode().series().equals(HttpStatus.Series.CLIENT_ERROR)) {
			
			String apiErrorCode = null;
			
			if(redirectURL != null) {
				apiErrorCode = new URLDecodeHelper(redirectURL).getQueryValue("error_description");
			}
			
			if(redirectURL == null || apiErrorCode == null) {
				log.error("認証認可API呼び出しエラー時のエラーコードが未設定です。 Location : {}", redirectURL);
				throw new ApplicationException(ApplicationErrors.UNEXPECTED, "認証APIエラーコード未設定");
			}
			 
			if(apiErrorCode.equals(CoreApiErrors.FAILED_OR_INVALID.getCode())
					 || apiErrorCode.equals(CoreApiErrors.UNAUTHORIZED.getCode())) {
				 // ログイン失敗。メンバー名またはパスワード不正
				model.addAttribute("apiLoginFailed", true);
				return "login/login";
			} else {
				log.info("アカウント状態不正 : HTTPステータス={} APIラーコード={}", response.getStatusCode(), apiErrorCode);
				//　アカウント状態不正
				return "redirect:/OneidStatus";
			}
		}
		
		final URLDecodeHelper urlDecodeHelper = new URLDecodeHelper(redirectURL);
		
		String loginType;
		
		try {
			// DIDアカウント判定のためログイン詳細情報を取得
			final String loginDescription = urlDecodeHelper.getQueryValueWithUrlDecode("description");
			log.debug("Login Description : {}", loginDescription);
			final Map<String, String> loginDescMap = new ObjectMapper().readValue(loginDescription, new TypeReference<Map<String, String>>(){});
			loginType = loginDescMap.get("login");
			log.debug("Login Type : {}", loginType);
			
		} catch (Exception e) {
			log.error("DIDアカウント判定処理時に予期せぬエラーが発生しました。");
			throw new ApplicationException(ApplicationErrors.UNEXPECTED, e, "DIDアカウント判定エラー");
		}
		
		if(loginType.equals("did")) {
			log.debug("DIDログインに成功しました。");
			log.debug("DID会員のSPP会員新規登録を開始します。");
			return sppRegisterAndLoginForDid(urlDecodeHelper.getQueryValue("did_token"), form.getMemberNameOrEmailAddr(), form.getPassword(), userAgent, guest.getDspp());
		} else {
			log.debug("SPPログインに成功しました。");	
			return response;			
		}
	}

	/**
	 * DID会員の場合、SPP会員登録後SPPログインを行う。
	 * @param didToken　DIDトークン
	 * @return ログイン結果レスポンス
	 */
	private ResponseEntity<String> sppRegisterAndLoginForDid(String didToken, String memberName, String password, String userAgent, String dspp) {
		final DidMemberDetails didMemberDetails = coreWebApiService.getDidInformation(didToken);
		coreWebApiService.registerSppMember(didMemberDetails.convertToSppMemberDetails(), true, false, didToken);
		return coreWebApiService.authorize(memberName, password, userAgent, dspp);
	}
	
	/**
	 * はじめて利用される方はこちらボタン押下時
	 */
	@RequestMapping(params = "register", method = RequestMethod.POST)
	public String firstTimeOfUse(@ModelAttribute(value = "emptyMailForm") @Valid EmptyMailForm form,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("hasErrorForRegister", true);
			return "login/login";
		}

		guest.setBirthDayYear(form.getBirthdayYear());
		guest.setBirthDayMonth(form.getBirthdayMonth());
		guest.setBirthDayDay(form.getBirthdayDay());

		return "redirect:/Login/emptymail";
	}

	/**
	 * 空メール送信確認ページ表示
	 */
	@RequestMapping(value = "emptymail", method = RequestMethod.GET)
	public String confirmEmptMailForm() {
		return "login/sendEmptyMail";
	}

}
