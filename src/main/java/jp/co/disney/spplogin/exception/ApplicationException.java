package jp.co.disney.spplogin.exception;

/**
 * アプリケーション共通の例外クラス
 *
 */
public class ApplicationException extends RuntimeException {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private String  errorCode;
	
	private String errorMessage;
	
	public ApplicationException(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	/**
	 * 画面表示用のエラーコードを取得する。
	 * 
	 * @return エラーコード
	 */
	public String getDispErrorCode() {
		return errorCode;
	}
	
	/**
	 * 画面表示用のエラーメッセージを取得する。
	 * 
	 * 
	 * @return エラーメッセージ
	 */
	public String getDispErrorMessage() {
		return errorMessage;
	}
	
}
