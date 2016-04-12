package jp.co.disney.spplogin.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * エラーページコントローラー
 * @author z2h7273
 *
 */
@Controller
public class ErrorController {

	@RequestMapping(value="/404", method=RequestMethod.GET)
	public String notFound() {
		return "common/404";
	}
	
	@RequestMapping(value="/500", method=RequestMethod.GET)
	public String internalServerError() {
		return "common/500";
	}
}
