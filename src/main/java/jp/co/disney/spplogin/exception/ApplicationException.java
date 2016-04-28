package jp.co.disney.spplogin.exception;

import java.text.MessageFormat;

import lombok.Getter;

/**
 * アプリケーション共通の例外クラス
 *
 */
public class ApplicationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	@Getter
	private ApplicationErrors error;
	@Getter
	private Throwable cause;
	@Getter
	private Object[] args;
	
	public ApplicationException(ApplicationErrors error) {
		super(getErrorMessage(error));
		this.error = error;
	}
	
	public ApplicationException(ApplicationErrors error, Throwable cause) {
		super(getErrorMessage(error), cause);
		this.error = error;
		this.cause = cause;
	}
	
	public ApplicationException(ApplicationErrors error, Object... args) {
		super(getErrorMessage(error, args));
		this.error = error;
		this.args = args;
	}
	
	public ApplicationException(ApplicationErrors error, Throwable cause, Object... args) {
		super(getErrorMessage(error, args), cause);
		this.error = error;
		this.cause = cause;
		this.args = args;
	}
	
	private static String getErrorMessage(ApplicationErrors error, Object... args) {
		return "[" + error.getCode() + "]" + MessageFormat.format(error.getMessage(), args);
	}
	
	private static String getErrorMessage(ApplicationErrors error) {
		return "[" + error.getCode() + "]" + error.getMessage();
	}
	
}
