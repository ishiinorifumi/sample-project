package jp.co.disney.spplogin.vo;

import lombok.Data;
import lombok.ToString;

/**
 * SPP会員情報詳細
 *
 */
@Data
@ToString
public class SppMemberDetails {
	private String sppId;
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
	private String address3;
	private String phoneNumber;
	private String ageBand;
	private Boolean emailActivation;
	private Boolean legalTou;
	private Boolean legalPp;
	private Boolean fob;
	private String memberStatus;
	private String registrationRequired;
	private String xmid;
}
