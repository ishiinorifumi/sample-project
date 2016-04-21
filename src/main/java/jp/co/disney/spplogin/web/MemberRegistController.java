package jp.co.disney.spplogin.web;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.disney.spplogin.web.form.MemberEntryForm;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("SPPEntry")
public class MemberRegistController {

	/**
	 * 性別
	 */
	@AllArgsConstructor
	private enum Gender {
		FAMALE("F"),
		MALE("M");
		
		private String code;
	}
	
	@ModelAttribute("memberEntryForm")
	public MemberEntryForm setUpForm() {
		final MemberEntryForm form = new  MemberEntryForm();
		form.setGender(Gender.FAMALE.code); //　性別はデフォルト"女性"
		return form;
	}
	
	@RequestMapping(value = "", params = "form", method = RequestMethod.GET)
	public String entryForm(Model model) {
		// TODO formパラメータに設定されたRedisキーからセッションを復元し、メールアドレス、誕生日を取得する。
		model.addAttribute("mailAddress", "test123@gmail.com");
		model.addAttribute("birthDay", "1991/12/26");
		return "memberregist/entry";
	}
	
	@RequestMapping("")
	public String entry(@ModelAttribute("memberEntryForm") @Valid MemberEntryForm form, BindingResult result, RedirectAttributes attributes, Model model) {
		if(result.hasErrors()){
			model.addAttribute("hasError", true);
			return "memberregist/entry";
		}
		
		return "redirect:/SPPEntry/confirm";
	}
	
	@RequestMapping("/confirm")
	public String confirm() {
		return "memberregist/confirm";
	}
	
	@RequestMapping("/complete")
	public String complete() {
		return "memberregist/complete";
	}
}
