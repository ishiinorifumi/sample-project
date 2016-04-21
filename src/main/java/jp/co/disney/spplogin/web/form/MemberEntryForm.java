package jp.co.disney.spplogin.web.form;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
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
	@Email
	private String mailAddress;
	
	/** 誕生日 */
	@NotBlank
	private String birthDay;
	
	/** パスワード */
	@Size(min = 6, max = 25)
	@Pattern(regexp = "[0-9A-B]*", message="パスワードは半角英数字を入力してください。")
	private String password;
	
	/** 確認用パスワード */
	private String confirmPassword;
	
	/** 性別 */
	@NotBlank
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
}
