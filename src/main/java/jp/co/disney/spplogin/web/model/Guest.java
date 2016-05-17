package jp.co.disney.spplogin.web.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.co.disney.spplogin.enums.Gender;
import jp.co.disney.spplogin.vo.SppMemberDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guest implements Serializable {

	private static final long serialVersionUID = -3581794323901867288L;
	
	/** 誕生日（年） */
	private String birthDayYear;
	/** 誕生日（月） */
	private String birthDayMonth;
	/** 誕生日（日） */
	private String birthDayDay;
	/** メールアドレス */
	private String mailAddress;
	/** 性別 */
	private Gender gender;
	/** パスワード */
	private String password;
	/** メンバー名 */
	private String memberName;
	/** 利用規約同意フラグ */
	private boolean legalTou;
	/**　個人情報について同意フラグ */
	private boolean legalPp;
	/** キャンペーンメール希望フラグ */
	private boolean fob;
	/** セッション復元フラグ */
	private boolean sessionRestored = false;
	
	@JsonIgnore
	public String getBirthDay() {
		return String.join("/", new String[]{birthDayYear, birthDayMonth, birthDayDay});
	}
	
	private String getBirthDayForRegister() {
		return String.format("%s-%02d-%02d", birthDayYear, Integer.valueOf(birthDayMonth), Integer.valueOf(birthDayDay));
	}
	
	public Guest copy() {
		return new Guest(
				this.birthDayYear,
				this.birthDayMonth,
				this.birthDayDay,
				this.mailAddress,
				this.gender,
				this.password,
				this.memberName,
				this.legalTou,
				this.legalPp,
				this.fob,
				this.sessionRestored);
	}
	
	public SppMemberDetails convertToSppMemberDetails() {
		final SppMemberDetails detail = new SppMemberDetails();
		detail.setDateOfBirth(getBirthDayForRegister());
		detail.setEmailAddress(this.getMailAddress());
		detail.setPassword(this.getPassword());
		detail.setGender(this.getGender().name());
		detail.setLegalTou(this.isLegalTou());
		detail.setLegalPp(this.isLegalPp());
		detail.setFob(this.isFob());
		return detail;
	}
}


