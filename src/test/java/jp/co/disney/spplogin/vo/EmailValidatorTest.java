package jp.co.disney.spplogin.vo;

import org.apache.commons.validator.EmailValidator;
import org.junit.Assert;
import org.junit.Test;


public class EmailValidatorTest {

	@Test
	public void validtor() throws Exception {
			Assert.assertTrue(EmailValidator.getInstance().isValid("miu..kanaseiji@gmail.com"));
	}
}
