package jp.co.disney.spplogin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ApplicationErrors {

	INVALID_MAIL_ADDRESS("SPC001", "お使いのメールアドレスは使用できません。：{0}"),
	INVALID_URL("SPC002", "ご指定のURLは有効ではありません。"),
	MEMBER_REGISTER_FAILED("SPC003", "SPP新規会員登録に失敗しました。:{0}"),
	INVALID_PARAMETER("SPC902", "パラメータの値が正しくありません。"),
	INVALID_OPERATION("SPC903", "不正な画面遷移によりこのページへのアクセスが試みられました。"),
	UNEXPECTED("SPC999", "想定外のエラーが発生しました。：{0}");
	
	@Getter
	private String code;
	
	@Getter
	private String message;
	
}
