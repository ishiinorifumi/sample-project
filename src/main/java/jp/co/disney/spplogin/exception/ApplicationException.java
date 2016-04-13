package jp.co.disney.spplogin.exception;

import lombok.Getter;

/**
 * アプリケーション共通の例外クラス
 *
 */
public class ApplicationException extends RuntimeException {

	/** serialVersionUID */
	private static final long serialVersionUID = -7643484724071537827L;

	@Getter
	private ApplicationErrors error;
	@Getter
	private Throwable cause;
	@Getter
	private Object[] args;
	
	public ApplicationException(ApplicationErrors error) {
		this.error = error;
	}
	
	public ApplicationException(ApplicationErrors error, Throwable cause) {
		this.error = error;
		this.cause = cause;
	}
	
	public ApplicationException(ApplicationErrors error, String... args) {
		this.error = error;
		this.args = args;
	}
	
	public ApplicationException(ApplicationErrors error, Throwable cause, String... args) {
		this.error = error;
		this.cause = cause;
		this.args = args;
	}
	
}
