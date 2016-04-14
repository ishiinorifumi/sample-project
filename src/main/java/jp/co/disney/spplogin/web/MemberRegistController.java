package jp.co.disney.spplogin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("SPPEntry")
public class MemberRegistController {

	@RequestMapping("")
	public String entry() {
		return "memberregist/entry";
	}
}
