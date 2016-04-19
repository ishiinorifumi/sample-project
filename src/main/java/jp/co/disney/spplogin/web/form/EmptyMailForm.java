package jp.co.disney.spplogin.web.form;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class EmptyMailForm {
	@NotBlank
	private String birthdayYear;
	@NotBlank
	private String birthdayMonth;
	@NotBlank
	private String birthdayDay;
}
