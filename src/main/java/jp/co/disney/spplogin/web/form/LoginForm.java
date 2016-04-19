package jp.co.disney.spplogin.web.form;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginForm {
	@NotBlank
	@Size(max=3)
	private String memberNameOrEmailAddr;
	
	@NotBlank
	private String password;
}
