package jp.co.disney.spplogin.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

	private final Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	@RequestMapping(value="/spring", method=RequestMethod.GET)
	public String hello() {
		logger.debug("debug log");
		logger.info("info log");
		logger.error("error log");
		return "Hello Spring Boot3!!";
	}
}
