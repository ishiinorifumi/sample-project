package jp.co.disney.spplogin.vo;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.ToString;

/**
 * DID会員情報詳細
 *
 */
@Data
@ToString
public class DidMemberDetails {
	private String swid;
	private String memberName;
	private String emailAddress;
	private String password;
	private String guardianEmailAddress;
	private String gender;
	private String prefectureCode;
	private String firstNameKanji;
	private String lastNameKanji;
	private String nameKana;
	private String dateOfBirth;
	private String postalCode;
	private String address1;
	private String address2;
	private String phoneNumber;
	private String ageBand;
	private Boolean emailActivation;
	private Boolean legalTou;
	private Boolean legalPp;
	private Boolean fob;
	
	/**
	 * DidMemberDetailsをSppMemberDetailsに変換する。
	 * @return
	 */
	public SppMemberDetails convertToSppMemberDetails() {
		final SppMemberDetails sppMemberDetails = new SppMemberDetails();
		BeanUtils.copyProperties(this, sppMemberDetails);
		return sppMemberDetails;
	}
}
