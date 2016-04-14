package jp.co.disney.spplogin.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
/***
 * 共通コントローラー
 *
 */
@Controller
public class CommonController {
	/**
	 * メンテナンスページ
	 * @return
	 */
	@RequestMapping(value="/maintenance", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public String maintenance() {
		return "common/maintenance";
	}

	/**
	 * サービス提供対象外端末アクセス時の
	 * @return
	 */
	@RequestMapping(value="/unsupported", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String  unsupportedModel() {
		return "common/unsupportedModel";
	}
}
