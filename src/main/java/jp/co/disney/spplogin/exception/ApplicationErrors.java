package jp.co.disney.spplogin.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ApplicationErrors {

	UNEXPECTED(HttpStatus.INTERNAL_SERVER_ERROR, "想定外のエラーが発生しました。：{0}");
	
	@Getter
	private HttpStatus status;
	
	@Getter
	private String message;
	
}
