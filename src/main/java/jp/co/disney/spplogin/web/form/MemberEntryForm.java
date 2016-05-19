package jp.co.disney.spplogin.web.form;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.StringUtils;

import lombok.Data;

/**
 * メンバー登録フォーム
 */
@Data
public class MemberEntryForm {
	/** メールアドレス */
	@NotBlank
	private String mailAddress;
	
	/** 誕生日 */
	@NotBlank
	private String birthDay;
	
	/** パスワード */
	@Size(min = 6, max = 25)
	@Pattern(regexp = "[0-9A-Za-z_#$%^&*()+{}\\[\\]|;~`<>?:.\\-@/]*", message="パスワードは半角英数字および一部記号（_#$%^&*()+{}[]|;~`<>?:.-@/）を入力してください。")
	private String password;
	
	/** 確認用パスワード */
	@NotBlank
	private String confirmPassword;
	
	/** 性別 */
	@Pattern(regexp = "[MF]{1}", message="性別の選択が正しくありません。")
	private String gender;
	
	/**
	 * パスワードとパスワード（確認）の一致バリデーション
	 * @return
	 */
	@AssertTrue(message = "パスワードとパスワード(確認)が異なります。")
	public boolean isValidConfirmPassword() {
		if(StringUtils.isEmpty(password)
				|| StringUtils.isEmpty(confirmPassword)) {
			return true;
		}
		
		return password.equals(confirmPassword);
	}
	
	@AssertTrue(message = "パスワードは英字と数字（または!#$%^&*などの記号）がそれぞれ1文字以上必要です。お名前や生年月日などの個人情報は使用しないでください。")
	public boolean isValidPassword() {
		if(StringUtils.isEmpty(password))
			return true;
		
		//final java.util.regex.Pattern validPattern = java.util.regex.Pattern.compile("^[0-9A-Za-z_#$%^&*()+{}\\[\\]|;~`<>?:.\\-@/]+$");
		final java.util.regex.Pattern allAlphabet = java.util.regex.Pattern.compile("^[a-zA-z]+$");
		final java.util.regex.Pattern allDigit = java.util.regex.Pattern.compile("^[0-9]+$");
		final java.util.regex.Pattern allSymbol = java.util.regex.Pattern.compile("^[_#$%^&*()+{}\\[\\]|;~`<>?:.\\-@/]+$");
		
		
		return !(allAlphabet.matcher(password).matches()
				|| allDigit.matcher(password).matches()
				|| allSymbol.matcher(password).matches());
	}
	
}
