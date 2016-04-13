package jp.co.disney.spplogin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ApplicationErrors {

	INVALID_MAIL_ADDRESS("SPC001", "お使いのメールアドレスは使用できません。：{0}"),
	INVALID_URL("SPC002", "ご指定のURLは有効ではありません。"),
	UNEXPECTED("SPC901", "想定外のエラーが発生しました。：{0}"),
	INVALID_PARAMETER("SPC902", "パラメータの値が正しくありません。");
	
	@Getter
	private String code;
	
	@Getter
	private String message;
	
}
