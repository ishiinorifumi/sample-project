package jp.co.disney.spplogin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CoreApiErrors {
	FAILED_OR_INVALID("010101"),
	UNAUTHORIZED("010667");
	
	@Getter
	private String code;
}
