package jp.co.disney.spplogin.controller;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jp.co.disney.spplogin.Application;
import jp.co.disney.spplogin.service.CoreWebApiService;
import jp.co.disney.spplogin.web.MemberRegistController;
import jp.co.disney.spplogin.web.model.Guest;
import lombok.AllArgsConstructor;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(Theories.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class MemberRegistControllerTest {

	@Rule
    public final MockitoRule rule = MockitoJUnit.rule();
	
	private MockMvc mockMvc;
	
	@Autowired
	private MockHttpSession mockHttpSession;
	
	@Autowired
    private WebApplicationContext wac;
	
	@Autowired
    private RedisTemplate<String, Guest> redisTemplate;
	
	@Autowired
	private Guest guest;
	
	@Mock
	@Autowired
    private CoreWebApiService coreWebApiService;
	
	@InjectMocks
	@Autowired
	private MemberRegistController controller;
	
    @Before
    public void before() throws Exception {
    	new TestContextManager(this.getClass()).prepareTestInstance(this);
    	this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();   	
    }
    
    
    @Test
    public void 登録入力画面表示() throws Exception {
    	final Guest wGuest = new Guest();
    	wGuest.setBirthDayYear("1972");
    	wGuest.setBirthDayMonth("10");
    	wGuest.setBirthDayDay("13");
    	wGuest.setMailAddress("test123@gmail.com");
    	final String id = UUID.randomUUID().toString();
    	redisTemplate.opsForValue().set(id, wGuest.copy());
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.get("/Regist")
    			.param("form", id)
    			.session(mockHttpSession))
    	.andExpect(status().isOk())
    	.andExpect(view().name(is("memberregist/entry")));
    	
    	assertThat(wGuest.getBirthDay(), is(this.guest.getBirthDay()));
    	assertThat(wGuest.getMailAddress(), is(this.guest.getMailAddress()));
    	assertThat(this.guest.isSessionRestored(), is(true));
    }
    
    @Theory
    public void 登録確認_バリデーションエラー(ValidationErrorTestFixture fixture) throws Exception {
    	
    	登録入力画面表示();
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Regist")
    			.param("password", fixture.password)
    			.param("confirmPassword", fixture.confirmPassword)
    			.param("gender", fixture.gender)
    			.session(mockHttpSession))
    	.andExpect(status().isOk())
    	.andExpect(model().hasErrors())
    	.andExpect(fixture.resultMatcher)
    	.andExpect(view().name(is("memberregist/entry")));
    	
    }
 
    @Test
    public void 登録確認_OK() throws Exception {
    	
    	登録入力画面表示();
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Regist")
    			.param("password", "test123")
    			.param("confirmPassword", "test123")
    			.param("gender", "F")
    			.session(mockHttpSession))
    	.andExpect(status().isFound())
    	.andExpect(redirectedUrl("/Regist/confirm"));
    	
    }

    /**
     * バリデーションエラーテスト用Fixture
     *
     */
    @AllArgsConstructor
    static class ValidationErrorTestFixture {
    	String password;
    	String confirmPassword;
    	String gender;
    	ResultMatcher resultMatcher;
    }

    @DataPoints
    public static ValidationErrorTestFixture[] geFixture() {
    	return new ValidationErrorTestFixture[] {
    			// パスワードが短い
    			new ValidationErrorTestFixture("12345", "12345", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Size")),
    			// パスワードが長い
    			new ValidationErrorTestFixture("12345678901234567890123456", "12345678901234567890123456", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Size")),
    			// パスワード使用禁止文字 記号
    			new ValidationErrorTestFixture("test@123", "test@123", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Pattern")),
    			// パスワード使用禁止文字 マルチバイト
    			new ValidationErrorTestFixture("あいうえおかきくけこ", "あいうえおかきくけこ", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Pattern")),
    			// パスワード確認フィールドと不一致
    			new ValidationErrorTestFixture("test123", "test124", "F", model().attributeHasFieldErrorCode("memberEntryForm", "validConfirmPassword", "AssertTrue")),
    			// 性別必須
    			new ValidationErrorTestFixture("test123", "test123", "", model().attributeHasFieldErrorCode("memberEntryForm", "gender", "NotBlank")),
    			// 性別は M or F
    			new ValidationErrorTestFixture("test123", "test123", "A", model().attributeHasFieldErrorCode("memberEntryForm", "gender", "Pattern"))
    	};
    }
    

}
