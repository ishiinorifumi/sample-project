package jp.co.disney.spplogin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
	
	@RequestMapping("/spplogin")
	public String getLogin(String dispRegsist, String dispService) {
		return null;
	}
	
}
