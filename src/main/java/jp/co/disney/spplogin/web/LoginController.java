package jp.co.disney.spplogin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	
	@RequestMapping(value="/spplogin", method=RequestMethod.GET)
	public String getLogin() {
		return "login/login";
	}
	
}
