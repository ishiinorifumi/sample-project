package jp.co.disney.spplogin.web.model;

import java.io.Serializable;

import jp.co.disney.spplogin.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guest implements Serializable {

	private static final long serialVersionUID = -2979728680928609792L;
	
	private String birthDay;
	private String mailAddress;
	private Gender gender;
	private String password;
	private String memberName;
	private boolean sessionRestored = false;
	
	public Guest copy() {
		return new Guest(this.birthDay, this.mailAddress, this.gender, this.password, this.memberName, this.sessionRestored);
	}
}


