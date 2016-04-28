package jp.co.disney.spplogin.web;

import javax.servlet.http.HttpSession;
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
import jp.co.disney.spplogin.util.SecureRandomUtil;
import jp.co.disney.spplogin.web.form.MemberEntryForm;
import jp.co.disney.spplogin.web.model.Guest;

@Controller
@RequestMapping("SPPEntry")
@SessionAttributes("scopedTarget.guest")
public class MemberRegistController {

	/** トランザクショントークンをセッションに格納するためのキー */
	private static String tranTokenSessionKey = "spplogin.tran-token-session-key";
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private Guest guest;
	
    @Autowired
    private RedisTemplate<String, Guest> redisTemplate;
	
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
			
			guest.setBirthDay(savedGuest.getBirthDay());
			
			// TODO メールアドレスの連携はどうやる？
			guest.setMailAddress("test-abc@aa.bb.cc.co.jp");
			
			// セッション復元済みフラグをたてる
			guest.setSessionRestored(true);
		}
		return "memberregist/entry";
	}
	
	/**
	 * 登録
	 * @param form
	 * @param result
	 * @param attributes
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String entry(@ModelAttribute("memberEntryForm") @Valid MemberEntryForm form, BindingResult result, RedirectAttributes attributes, Model model) {
		if(result.hasErrors()){
			model.addAttribute("hasError", true);
			return "memberregist/entry";
		}
		
		guest.setGender(Gender.valueOf(form.getGender()));
		guest.setPassword(form.getPassword());
		
		return "redirect:/SPPEntry/confirm";
	}
	
	/**
	 * 登録確認
	 * @return
	 */
	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public String confirm() {
		final String tranToken = SecureRandomUtil.genToken();
		session.setAttribute(tranTokenSessionKey, tranToken);
		return "memberregist/confirm";
	}
	
	/**
	 * 登録内容修正
	 * @return
	 */
	@RequestMapping(value = "/register", params="modify", method = RequestMethod.POST)
	public String modify() {
		return "redirect:/SPPEntry?form";
	}

	/**
	 * 登録処理
	 * @param attributes
	 * @param sessionStatus
	 * @return
	 */
	@RequestMapping(value = "/register", params="register", method = RequestMethod.POST)
	public String register(RedirectAttributes attributes, SessionStatus sessionStatus) {
		//TODO Core APIによる会員登録処理
		
		final Guest member = guest.copy();
		member.setMemberName("_apldk18d");
		sessionStatus.setComplete();
		attributes.addFlashAttribute("member", member);
		return "redirect:/SPPEntry/complete";
	}
	
	/**
	 * 登録完了画面
	 * @return
	 */
	@RequestMapping(value="/complete", method = RequestMethod.GET)
	public String complete() {
		return "memberregist/complete";
	}
}
