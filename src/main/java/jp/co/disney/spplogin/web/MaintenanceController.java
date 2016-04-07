package jp.co.disney.spplogin.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class MaintenanceController {

	@RequestMapping(value="/maintenance", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public String getMaintenance() {
		return "common/maintenance";
	}
}
