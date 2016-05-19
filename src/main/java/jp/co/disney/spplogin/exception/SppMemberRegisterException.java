package jp.co.disney.spplogin.exception;

import java.util.Map;

import lombok.Getter;

public class SppMemberRegisterException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	@Getter
	private Map<String, String> errorDetail;
	
	public SppMemberRegisterException(Map<String, String> detail) {
		super(ApplicationErrors.MEMBER_REGISTER_FAILED, detail.get("spp_message") + "(" + detail.get("code") + ")");
		this.errorDetail = detail;
	}
}
