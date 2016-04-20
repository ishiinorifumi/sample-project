package jp.co.disney.spplogin.web.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class EmptyMailForm {
	@NotBlank
	private String birthdayYear;
	@NotBlank
	private String birthdayMonth;
	@NotBlank
	private String birthdayDay;
	
	@AssertTrue(message = "生年月日の日付が正しくありません。")
	public boolean isValidDate() {
		if(StringUtils.isEmpty(birthdayYear) ||
				StringUtils.isEmpty(birthdayMonth) ||
				StringUtils.isEmpty(birthdayDay)) {
			
			return true;
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d");
		formatter.setLenient(false);
		try {
			formatter.parse(this.birthdayYear + "/" + this.birthdayMonth + "/" + this.birthdayDay);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}
}
