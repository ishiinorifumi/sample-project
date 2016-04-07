package jp.co.disney.spplogin.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jp.co.disney.spplogin.exception.ApplicationException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ApplicationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleApplicationException(ApplicationException e) {
        return new ModelAndView("common/error")
        		.addObject("errorMessage", e.getDispErrorMessage())
                .addObject("errorCode", e.getDispErrorCode());
	}
    
    @ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleRuntimeException(RuntimeException e) {
        return  "common/error";
	}
}
