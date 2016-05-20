package jp.co.disney.spplogin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CoreApiErrors {
	/** SPP会員登録 パスワードフォーマットエラーコード */
	INVALID_PASSWORD_FORMAT("010930"),
	/** SPP会員登録 メンバー名使用不可エラーコード */
	UNUSABLE_MEMBER_NAME_ERROR("010776"),
	/** メールアドレスフォーマットエラー */
	INVALID_FORMAT_MAIL_ADDRESS("010931"),
	FAILED_OR_INVALID("010101"),
	UNAUTHORIZED("010667");
	
	@Getter
	private String code;
}
