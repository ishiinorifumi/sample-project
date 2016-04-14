package jp.co.disney.spplogin.web;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jp.co.disney.spplogin.exception.ApplicationErrors;
import jp.co.disney.spplogin.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * 共通例外ハンドラー
 *
 */
@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ApplicationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleApplicationException(ApplicationException ex) {
        return handleError(ex.getError(), ex, ex.getArgs());
	}
    
    @ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleRuntimeException(RuntimeException ex) {
        return  handleError(ApplicationErrors.UNEXPECTED, ex, ex.toString());
	}
    
    private ModelAndView handleError(ApplicationErrors error, Exception ex, Object... args) {
    	String message;
		if(args != null) {
			message = MessageFormat.format(error.getMessage(), args);
		} else {
			message = error.getMessage();
		}
		
		log.error(message, ex);
		
    	return new ModelAndView("common/error")
    			.addObject("errorCode", error.getCode())
        		.addObject("errorMessage", message);
    }
}
