package jp.co.disney.spplogin.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import jp.co.disney.spplogin.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("unit")
public class RandomHelperTest {

	@Autowired
	private RandomHelper randomHelper;
	
	@Test
	public void randomIdメソッド() throws Exception {
		final String actual = randomHelper.randomID();
		final Matcher m = Pattern.compile("^[0-9a-z]+$").matcher(actual);
		Assert.assertTrue(m.matches());
	}
}
