package jp.co.disney.spplogin.helper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.disney.spplogin.enums.CoreApiErrors;
import jp.co.disney.spplogin.exception.ApplicationErrors;
import jp.co.disney.spplogin.exception.ApplicationException;
import jp.co.disney.spplogin.exception.SppMemberRegisterException;
import jp.co.disney.spplogin.service.CoreWebApiService;
import jp.co.disney.spplogin.vo.SppMemberDetails;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailAddressValidator {

	/** 許容ドメイン */
	private static final Set<String> validDomains = new HashSet<>();;
	
	static {
		validDomains.add("docomo.ne.jp");
		validDomains.add("disneymobile.ne.jp");
		validDomains.add("softbank.ne.jp");
		validDomains.add("i.softbank.jp");
		validDomains.add("d.vodafone.ne.jp");
		validDomains.add("h.vodafone.ne.jp");
		validDomains.add("t.vodafone.ne.jp");
		validDomains.add("c.vodafone.ne.jp");
		validDomains.add("r.vodafone.ne.jp");
		validDomains.add("k.vodafone.ne.jp");
		validDomains.add("n.vodafone.ne.jp");
		validDomains.add("s.vodafone.ne.jp");
		validDomains.add("q.vodafone.ne.jp");
		validDomains.add("ezweb.ne.jp");
		validDomains.add("disney.ne.jp");
		validDomains.add("gmail.com");
	}
	
	@Autowired
	private CoreWebApiService coreWebApiService;
	
	/**
	 * <pre>
	 * メールアドレス有効性判定。
	 * 不正なメールアドレスの場合例外を投げる。
	 * </pre>
	 * @param emailAddress
	 * @return
	 */
	public boolean validate(String emailAddress) {
		
		try{
			// メールアドレス形式および重複登録はCoreAPIによりチェックする。
			final SppMemberDetails details = new SppMemberDetails();
			details.setEmailAddress(emailAddress);
			// メールアドレス以外はダミー値を設定
			details.setPassword("test@1234567");
			details.setGender("M");
			details.setDateOfBirth("1989-01-01");
			details.setFob(true);
			details.setLegalPp(true);
			details.setLegalTou(true);
			details.setPrefectureCode("13");
			
			coreWebApiService.registerSppMember(details, false, true, null);
			
		} catch (SppMemberRegisterException e) {
			final Map<String, String> error = e.getErrorDetail();
			final String code = error.get("code");
			if(code.equals(CoreApiErrors.UNUSABLE_MEMBER_NAME_ERROR.getCode())) {
				// メールアドレス重複
				throw new ApplicationException(ApplicationErrors.DUPLICATE_MAIL_ADDRESS, emailAddress);
			} else if(code.equals(CoreApiErrors.INVALID_FORMAT_MAIL_ADDRESS.getCode())){
				// メールアドレスフォーマット不正
				throw new ApplicationException(ApplicationErrors.INVALID_FORMAT_MAIL_ADDRESS, emailAddress);
			} else {
				throw new RuntimeException(error.get("spp_message"));
			}
		}
		
		final String domain = emailAddress.split("@")[1];
		
		if(!validDomains.contains(domain)){
			// 許容ドメインでない
			throw new ApplicationException(ApplicationErrors.INVALID_DOMAIN_MAIL_ADDRESS, emailAddress);
		}
		
		return true;
	}
	
}