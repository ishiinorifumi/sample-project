package jp.co.disney.spplogin.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.disney.spplogin.enums.Gender;
import jp.co.disney.spplogin.exception.ApplicationErrors;
import jp.co.disney.spplogin.exception.ApplicationException;
import jp.co.disney.spplogin.exception.SppMemberRegisterException;
import jp.co.disney.spplogin.service.CoreWebApiService;
import jp.co.disney.spplogin.vo.SppMemberDetails;
import jp.co.disney.spplogin.web.form.MemberEntryForm;
import jp.co.disney.spplogin.web.model.Guest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("Regist")
@SessionAttributes("scopedTarget.guest")
public class MemberRegistController {
	
	/** SPP会員登録 パスワードフォーマットエラーコード */
	private final static String INVALID_PASSWORD_ERROR_CODE = "010930";
	
	/** SPP会員登録 メンバー名使用不可エラーコード */
	private final static String UNUSABLE_MEMBER_NAME_ERROR_CODE = "010776";
	
	@Autowired
	private Guest guest;
	
    @Autowired
    private RedisTemplate<String, Guest> redisTemplate;
    
    @Autowired
    private CoreWebApiService coreWebApiService;
	
	@ModelAttribute("memberEntryForm")
	public MemberEntryForm setUpForm() {
		final MemberEntryForm form = new  MemberEntryForm();
		form.setGender(guest.getGender() == null ? Gender.F.name() : guest.getGender().name()); //　性別はデフォルト"女性"
		form.setMailAddress(guest.getMailAddress());
		form.setBirthDay(guest.getBirthDay());
		return form;
	}
	
	@ModelAttribute("guest")
	public Guest setUpGuest() {
		return guest;
	}
	
	/**
	 * 登録画面
	 * @param form
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "", params = "form", method = RequestMethod.GET)
	public String entryForm(@RequestParam(required = true) String form, Model model) {
		if(!guest.isSessionRestored()){
			final Guest savedGuest = redisTemplate.opsForValue().get(form);
			if(savedGuest == null)
				throw new ApplicationException(ApplicationErrors.INVALID_URL);
			
			redisTemplate.delete(form);
			
			guest.setBirthDayYear(savedGuest.getBirthDayYear());
			guest.setBirthDayMonth(savedGuest.getBirthDayMonth());
			guest.setBirthDayDay(savedGuest.getBirthDayDay());
			
			//guest.setMailAddress("seiji.takahashi.909@ctc-g.co.jp");
			guest.setMailAddress(savedGuest.getMailAddress());
			
			// セッション復元済みフラグをたてる
			guest.setSessionRestored(true);
			
		}
		
		return "memberregist/entry";
	}
	
	/**
	 * 登録内容チェック
	 * @param form
	 * @param result
	 * @param attributes
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String entry(@ModelAttribute("memberEntryForm") @Valid MemberEntryForm form, BindingResult result, RedirectAttributes attributes, Model model) {
		
		checkInvalidOperation();
		
		if(result.hasErrors()){
			model.addAttribute("hasError", true);
			return "memberregist/entry";
		}
		
		guest.setGender(Gender.valueOf(form.getGender()));
		guest.setPassword(form.getPassword());
		
		try {
			SppMemberDetails details = guest.convertToSppMemberDetails();
			details.setPassword(form.getPassword());
			details.setGender(form.getGender());
			details.setFob(true);
			details.setLegalPp(true);
			details.setLegalTou(true);
			details.setPrefectureCode("13");
			// SPP会員新規登録判定APIコール
			coreWebApiService.registerSppMember(details, false, true, null);
		} catch(SppMemberRegisterException e) {
			log.warn("SPP会員新規登録判定でエラーとなりました。 {}", e.getErrorDetail());
			final String errorCode = e.getErrorDetail().get("code");
			String dispErrorMessage = "";
			switch(errorCode) {
				case INVALID_PASSWORD_ERROR_CODE :
					dispErrorMessage = "パスワードは6文字以上25文字以下で登録してください。英字と数字（または!#$%^&*などの記号）がそれぞれ1文字以上必要です。お名前や生年月日などの個人情報は使用しないでください。";
					break;
				case UNUSABLE_MEMBER_NAME_ERROR_CODE :
					dispErrorMessage = "このメールアドレスは既に使用されています。";
					break;
				default :
					dispErrorMessage = e.getErrorDetail().get("spp_message");
					break;
			}
			 // ログイン失敗。メンバー名またはパスワード不正
			model.addAttribute("memberRegistApiErrorMsg", dispErrorMessage);
			return "memberregist/entry";
		}
		
		return "redirect:/Regist/confirm";
	}
	
	/**
	 * 登録確認
	 * @return
	 */
	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public String confirm(Model model) {
		checkInvalidOperation();
		return "memberregist/confirm";
	}
	
	/**
	 * 登録内容修正
	 * @return
	 */
	@RequestMapping(value = "/register", params="modify", method = RequestMethod.POST)
	public String modify() {
		checkInvalidOperation();
		return "redirect:/Regist?form";
	}

	/**
	 * 登録処理
	 * @param attributes
	 * @param sessionStatus
	 * @return
	 */
	@RequestMapping(value = "/register", params="register", method = RequestMethod.POST)
	public String register(@RequestParam(required = false) boolean fob, RedirectAttributes attributes) {
		checkInvalidOperation();
		
		guest.setLegalTou(true);
		guest.setLegalPp(true);
		guest.setFob(fob);
		
		final SppMemberDetails req = guest.convertToSppMemberDetails();
		// TODO 都道府県コードは現状必須のため固定で設定する。
		req.setPrefectureCode("13");
		final SppMemberDetails result = coreWebApiService.registerSppMember(req, true, true, null);
		
		final Guest member = guest.copy();
		
		// TODO APIで登録されたメンバー名
		member.setMemberName(result.getMemberName());
		//member.setMemberName("_apldk18d");
		attributes.addFlashAttribute("member", member);
		return "redirect:/Regist/finish";
	}
	
	/**
	 * 登録完了画面
	 * @return
	 */
	@RequestMapping(value="/finish", method = RequestMethod.GET)
	public String finish(SessionStatus sessionStatus) {
		checkInvalidOperation();
		// セッションを破棄
		sessionStatus.setComplete();
		return "memberregist/finish";
	}
	
	/**
	 * メンバー登録完了後に各種メンバー登録ページにアクセスされた場合のチェック
	 */
	private void checkInvalidOperation() {
		if(!guest.isSessionRestored()){
			throw new ApplicationException(ApplicationErrors.INVALID_OPERATION);
		}
	}
}
