package jp.co.disney.spplogin.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ApplicationErrors {

	UNEXPECTED(HttpStatus.INTERNAL_SERVER_ERROR, "想定外のエラーが発生しました。：{0}"),
	INVALID_MAIL_ADDRESS(HttpStatus.INTERNAL_SERVER_ERROR, "お使いのメールアドレスは使用できません。"),
	INVALID_URL(HttpStatus.INTERNAL_SERVER_ERROR, "ご指定のURLは有効ではありません。");
	
	@Getter
	private HttpStatus status;
	
	@Getter
	private String message;
	
}
