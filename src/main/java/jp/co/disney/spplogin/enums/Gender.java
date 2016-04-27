package jp.co.disney.spplogin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性別
 */
@AllArgsConstructor
public enum Gender {
	F("女性"),
	M("男性");
	
	@Getter
	private String decode;
}
