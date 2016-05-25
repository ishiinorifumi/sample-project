package jp.co.disney.spplogin.controller;


import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
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
import jp.co.disney.spplogin.enums.CoreApiErrors;
import jp.co.disney.spplogin.exception.SppMemberRegisterException;
import jp.co.disney.spplogin.helper.EmailAddressValidator;
import jp.co.disney.spplogin.helper.RandomHelper;
import jp.co.disney.spplogin.service.CoreWebApiService;
import jp.co.disney.spplogin.vo.SppMemberDetails;
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
	
	@Autowired
	private RandomHelper randomHelper;
	
	@Spy
	private EmailAddressValidator emailAddressValidator;
	
	@Mock
	private CoreWebApiService coreWebApiService;
	
	@InjectMocks
	@Autowired
	private MemberRegistController controller;
	
    @Before
    public void before() throws Exception {
    	new TestContextManager(this.getClass()).prepareTestInstance(this);
    	this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    	MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void 登録入力画面表示() throws Exception {
    	
    	doReturn(true).when(emailAddressValidator).validate(anyString());
    	
    	final Guest wGuest = new Guest();
    	wGuest.setBirthDayYear("1972");
    	wGuest.setBirthDayMonth("10");
    	wGuest.setBirthDayDay("13");
    	wGuest.setMailAddress("test123@gmail.com");
    	final String id = randomHelper.randomID();
    	redisTemplate.opsForValue().set(id, wGuest.copy());
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.get("/Regist")
    			.param("form", id)
    			.session(mockHttpSession))
    	.andExpect(status().isOk())
    	.andExpect(view().name(is("memberregist/entry")));
    	
    	assertThat(wGuest.getBirthDay(), is(this.guest.getBirthDay()));
    	assertThat(wGuest.getMailAddress(), is(this.guest.getMailAddress()));
    	assertThat(this.guest.isSessionRestored(), is(true));
    	
    	verify(emailAddressValidator).validate(anyString());
    }
    
    @Test
    public void 登録確認_CoreAPIパスワードフォーマットエラー() throws Exception {
    	登録入力画面表示();
    	
    	final Map<String, String> errorDetail = new HashMap<>();
    	errorDetail.put("code", CoreApiErrors.INVALID_PASSWORD_FORMAT.getCode());
    	final SppMemberRegisterException exception = new SppMemberRegisterException(errorDetail);
    	
    	when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString())).thenThrow(exception);
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Regist")
    			.param("password", "test123")
    			.param("confirmPassword", "test123")
    			.param("gender", "M")
    			.session(mockHttpSession))
    	.andExpect(status().isOk())
    	.andExpect(model().attribute("memberRegistApiErrorMsg", is("パスワードは6文字以上25文字以下で登録してください。英字と数字（または!#$%^&*などの記号）がそれぞれ1文字以上必要です。お名前や生年月日などの個人情報は使用しないでください。")));
    }
    
    @Test
    public void 登録確認_CoreAPIメールアドレス二重登録() throws Exception {
    	登録入力画面表示();
    	
    	final Map<String, String> errorDetail = new HashMap<>();
    	errorDetail.put("code", CoreApiErrors.UNUSABLE_MEMBER_NAME_ERROR.getCode());
    	final SppMemberRegisterException exception = new SppMemberRegisterException(errorDetail);
    	
    	when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString())).thenThrow(exception);
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Regist")
    			.param("password", "test123")
    			.param("confirmPassword", "test123")
    			.param("gender", "M")
    			.session(mockHttpSession))
    	.andExpect(status().isOk())
    	.andExpect(model().attribute("memberRegistApiErrorMsg", is("このメールアドレスは既に使用されています。")));
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
    public void 登録確認OK_許容文字その１() throws Exception {
    	
    	登録入力画面表示();
    	
    	when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString())).thenReturn(new SppMemberDetails());
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Regist")
    			.param("password", "_#$%^&*()+{}[]|;azAZ09")
    			.param("confirmPassword", "_#$%^&*()+{}[]|;azAZ09")
    			.param("gender", "F")
    			.session(mockHttpSession))
    	.andExpect(status().isFound())
    	.andExpect(redirectedUrl("/Regist/confirm"));
    }

    @Test
    public void 登録確認OK_許容文字その２() throws Exception {
    	
    	登録入力画面表示();
    	
    	when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString())).thenReturn(new SppMemberDetails());
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Regist")
    			.param("password", "~`<>?:.-@/abc123")
    			.param("confirmPassword", "~`<>?:.-@/abc123")
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
    			// パスワード未設定
    			new ValidationErrorTestFixture("", "12345", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Size")),
    			// パスワードが短い
    			new ValidationErrorTestFixture("a1234", "a1234", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Size")),
    			// パスワードが長い
    			new ValidationErrorTestFixture("a1234567890123456789012345", "a1234567890123456789012345", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Size")),
    			// パスワード使用禁止文字 記号
    			new ValidationErrorTestFixture("test!123", "tes!123", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Pattern")),
    			// パスワード使用禁止文字 マルチバイト
    			new ValidationErrorTestFixture("あいうえおかきくけこ", "あいうえおかきくけこ", "F", model().attributeHasFieldErrorCode("memberEntryForm", "password", "Pattern")),
    			// パスワード（確認）必須
    			new ValidationErrorTestFixture("test123", "", "M", model().attributeHasFieldErrorCode("memberEntryForm", "confirmPassword", "NotBlank")),
    			// パスワード確認フィールドと不一致
    			new ValidationErrorTestFixture("test123", "test124", "F", model().attributeHasFieldErrorCode("memberEntryForm", "validConfirmPassword", "AssertTrue")),
    			// パスワードがアルファベットだけ
    			new ValidationErrorTestFixture("abcAbc", "abcAbc", "F", model().attributeHasFieldErrorCode("memberEntryForm", "validPassword", "AssertTrue")),
    			// パスワードが数値だけ
    			new ValidationErrorTestFixture("0123456789", "0123456789", "F", model().attributeHasFieldErrorCode("memberEntryForm", "validPassword", "AssertTrue")),
    			// パスワードが記号だけ
    			new ValidationErrorTestFixture("_#$%^&*()+{}[]|;~`<>?:.-@", "_#$%^&*()+{}[]|;~`<>?:.-@", "F", model().attributeHasFieldErrorCode("memberEntryForm", "validPassword", "AssertTrue")),
    			// 性別必須
    			new ValidationErrorTestFixture("test123", "test123", "", model().attributeHasFieldErrorCode("memberEntryForm", "gender", "Pattern")),
    			// 性別は M or F
    			new ValidationErrorTestFixture("test123", "test123", "A", model().attributeHasFieldErrorCode("memberEntryForm", "gender", "Pattern"))
    	};
    }
    

}
