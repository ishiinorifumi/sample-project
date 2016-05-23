package jp.co.disney.spplogin.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jp.co.disney.spplogin.Application;
import jp.co.disney.spplogin.service.CoreWebApiService;
import jp.co.disney.spplogin.web.LoginController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class LoginControllerTest {

	private static final String USER_AGENT = "Mozilla /5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B5110e Safari/601.1";
	private static final String DSPP = "m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU/tMLA=";
	
	@Rule
    public final MockitoRule rule = MockitoJUnit.rule();
	
	private MockMvc mockMvc;
	
	@Autowired
	private MockHttpSession mockHttpSession;
	
	@Autowired
    private WebApplicationContext wac;
	
	@Mock
	@Autowired
    private CoreWebApiService coreWebApiService;
	
	@InjectMocks
	@Autowired
	private LoginController controller;
	
    @Before
    public void setUp() {
        //this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller).build();
    	this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    	//mockHttpSession = new MockHttpSession(wac.getServletContext(), UUID.randomUUID().toString());
    	
    }
    
    @Test
    public void ログインフォーム表示() throws Exception {
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.get("/Login")
    			.param("dspp", DSPP)
    			.param("service_name", "サービス名称")
    			.session(mockHttpSession))
    	.andExpect(status().isOk())
    	.andExpect(view().name(is("login/login")));
    }
	
    @Test
    public void ログイン_login_or_registerパラメータが未設定() throws Exception {
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.header("User-Agent", USER_AGENT)
    			.param("memberNameOrEmailAddr", "")
    			.param("password", "pass"))
    	//.andDo(MockMvcResultHandlers.print())
    	.andExpect(status().is4xxClientError());
    }
    
    @Test
    public void ログイン_バリデーションエラー_必須項目未入力() throws Exception {
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.header("User-Agent", USER_AGENT)
    			.param("login", "")
    			.param("memberNameOrEmailAddr", "")
    			.param("password", ""))
    	//.andDo(MockMvcResultHandlers.print())
    	.andExpect(status().isOk())
    	.andExpect(model().hasErrors())
    	.andExpect(model().attributeHasFieldErrorCode("loginForm", "memberNameOrEmailAddr", "NotBlank"))
    	.andExpect(model().attributeHasFieldErrorCode("loginForm", "password", "NotBlank"))
    	.andExpect(model().attribute("hasErrorForLogin", is(true)));

    }
    
    @Test
    public void ログイン_バリデーションエラー_メンバー名文字数上限オーバー() throws Exception {
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.header("User-Agent", USER_AGENT)
    			.param("login", "")
    			.param("memberNameOrEmailAddr", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
    			.param("password", "pass"))
    	//.andDo(MockMvcResultHandlers.print())
    	.andExpect(status().isOk())
    	.andExpect(model().hasErrors())
    	.andExpect(model().attributeHasFieldErrorCode("loginForm", "memberNameOrEmailAddr", "Size"))
    	.andExpect(model().attribute("hasErrorForLogin", is(true)));

    }
    
    @Test
    public void SPP3会員ログイン成功() throws Exception {
    	ログインフォーム表示();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI("http://dev2.ssopen.disney.co.jp/auidauth/SessionKeyInfoUpd/?access_token=QrS8-2zCE_2ZUhvDlA3_i7RPGCopMpEr9_XgvX9mi-_5brRKk-VUR5wrSeIHqDweJ70woENHNICiXWrMv0v9Oa4YQcLZmqCnDS57fqLw35XXfUk1DlEficvDNyRPRU9QItmj-NlKiZjNsKydaJiRKj8LGHuhyTWUXhx-QbASHVD-aMl1f9l0ifAVlqLkpRUCGDMhvd701MOsIENokLBCQ1YnTGq39V3QWENSSrRX4U_3qLfxespDqoXkRnHzX3qZlvIkEIx52UytUY2E-JCEus3okBcXyrjfLCv1KHZldwevQJteCaaxR1CW3BovrYKnmzOJP2aPezkBLQuVkPt_JQ&did_token=eyJhY2Nlc3NfdG9rZW4iOiJpS2R0aTBKMERDcy02UmpMaGpzOW9RIiwiY2xpZW50SWQiOiJXREktSlAuSkEuU1BQLkdDLVNUQUdFIiwicmVmcmVzaF90b2tlbiI6IlV1akNRWldXTjR6Yzh4QUVKcFVWQ2ciLCJzd2lkIjoiNDlFMkY1MUUtQ0ExQS00RDNCLTkyQUMtQUM1MEFEQzIwNTE3IiwidHRsIjo3MjAwfQ&id_token=eyJhbGciOiJSUzI1NiIsICJ0eXAiOiJKV1QifQ.eyJhcHAiOm51bGwsImF0X2hhc2giOiJRclM4LTJ6Q0VfMlpVaHZEIiwiYXVkIjoiZHNoYXJfYUw3Zm9SMnlFbiIsImJkIjpudWxsLCJjYXJyaWVyIjpudWxsLCJkdmlkIjpudWxsLCJleHAiOjE0NjU5ODAwNDIsImlhdCI6MTQ2Mjk1NjA0MiwiaWNjaWQiOm51bGwsImltZWkiOm51bGwsImlzcyI6Imh0dHBzOi8vc3NvcGVuLmRpc25leS5jby5qcC8iLCJtb2RlbCI6ImlQaG9uZSIsIm5vbmNlIjoibi0wUzZfV3pBMk1qIiwib3BlIjowLCJvcyI6Ik9TIDlfMSIsInNwcGlkIjoiMDI0MDAwMDAyIiwic3VpZCI6bnVsbCwic3dpZCI6IjQ5RTJGNTFFLUNBMUEtNEQzQi05MkFDLUFDNTBBREMyMDUxNyIsInVzZXJfaWQiOiIwMjQwMDAwMDIiLCJ1dWlkIjpudWxsfQ.UJm5UEasRLKfnW6Ew5iBBtlVyW2FaDm11YCoKsF45mieqRBYA5EIJJPX2V-ErZQVXE8DLrGXKdr4YQJIqlTJZA&description=eyJlbWFpbF9hZGRyZXNzX2V4aXN0IjpmYWxzZSwiaXNfYm91bmNlZCI6ZmFsc2UsImlzX2luY29tcGxldGUiOmZhbHNlLCJpc19teW1lbnVfaW1vZGVfbGVmdCI6ZmFsc2UsImlzX215bWVudV9sZWZ0IjpmYWxzZSwibG9naW4iOiJzcHAzIiwibG9naW5fc3RhdHVzIjoiTUFSS0VUSU5HX1JFUVVJUkVEX0ZPQiJ9&state=m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU%2FtMLA%3D&token_type=Bearer"));
        HttpStatus status = HttpStatus.FOUND;
        ResponseEntity<String>  response = new ResponseEntity<>(null, headers, status);
    	Mockito.when(coreWebApiService.authorize("test@gmail.com", "123456", USER_AGENT, DSPP)).thenReturn(response);
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.header("User-Agent", USER_AGENT)
    			.param("login", "")
    			.param("memberNameOrEmailAddr", "test@gmail.com")
    			.param("password", "123456")
    			.session(mockHttpSession))
    	.andExpect(status().isFound());
    }
    
    @Test
    public void ログイン失敗_メンバー名が正しくない() throws Exception {
    	ログインフォーム表示();
    	HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI("http://dev2.ssopen.disney.co.jp/auidauth/SessionKeyInfoUpd/?error_description=010667&state=m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU%2FtMLA%3D&error=invalid_request"));
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ResponseEntity<String>  response = new ResponseEntity<>(null, headers, status);
    	Mockito.when(coreWebApiService.authorize("test@gmail.com", "123456", USER_AGENT, DSPP)).thenReturn(response);
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.header("User-Agent", USER_AGENT)
    			.param("login", "")
    			.param("memberNameOrEmailAddr", "test@gmail.com")
    			.param("password", "123456")
    			.session(mockHttpSession))
    	.andExpect(status().isOk())
    	.andExpect(view().name(is("login/login")))
    	.andExpect(model().attribute("apiLoginFailed", is(true)));
    }
    
    @Test
    public void ログイン失敗_パスワードが正しくない() throws Exception {
    	ログインフォーム表示();
    	HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI("http://dev2.ssopen.disney.co.jp/auidauth/SessionKeyInfoUpd/?error_description=010101&state=m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU%2FtMLA%3D&error=invalid_request"));
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ResponseEntity<String>  response = new ResponseEntity<>(null, headers, status);
    	Mockito.when(coreWebApiService.authorize("test@gmail.com", "123456", USER_AGENT, DSPP)).thenReturn(response);
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.header("User-Agent", USER_AGENT)
    			.param("login", "")
    			.param("memberNameOrEmailAddr", "test@gmail.com")
    			.param("password", "123456")
    			.session(mockHttpSession))
    	.andExpect(status().isOk())
    	.andExpect(view().name(is("login/login")))
    	.andExpect(model().attribute("apiLoginFailed", is(true)));
    }
    
    @Test
    public void ログイン失敗_アカウント状態不正_MASE() throws Exception {
    	ログインフォーム表示();
    	HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI("http://dev2.ssopen.disney.co.jp/auidauth/SessionKeyInfoUpd/?error_description=010102&state=m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU%2FtMLA%3D&error=invalid_request"));
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ResponseEntity<String>  response = new ResponseEntity<>(null, headers, status);
    	Mockito.when(coreWebApiService.authorize("test@gmail.com", "123456", USER_AGENT, DSPP)).thenReturn(response);
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.header("User-Agent", USER_AGENT)
    			.param("login", "")
    			.param("memberNameOrEmailAddr", "test@gmail.com")
    			.param("password", "123456")
    			.session(mockHttpSession))
    	.andExpect(status().isFound())
    	.andExpect(redirectedUrl("/OneidStatus"));
    }
    
    @Test
    public void はじめて登録される方はこちら_正常系() throws Exception {
    	ログインフォーム表示();
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.param("register", "")
    			.param("birthdayYear", "1975")
    			.param("birthdayMonth", "12")
    			.param("birthdayDay", "25")
    			.session(mockHttpSession))
    	.andExpect(status().isFound())
    	.andExpect(redirectedUrl("/Login/emptymail"));
    }
    
    @Test
    public void はじめて登録される方はこちら_バリデーションエラー_誕生日日付不正() throws Exception {
    	ログインフォーム表示();
    	this.mockMvc.perform(MockMvcRequestBuilders.post("/Login")
    			.param("register", "")
    			.param("birthdayYear", "1975")
    			.param("birthdayMonth", "2")
    			.param("birthdayDay", "29")
    			.session(mockHttpSession))
    	//.andDo(MockMvcResultHandlers.print())
    	.andExpect(status().isOk())
    	.andExpect(model().hasErrors())
    	.andExpect(model().attributeHasFieldErrorCode("emptyMailForm", "validDate", "AssertTrue"))
    	.andExpect(model().attribute("hasErrorForRegister", is(true)))
    	.andExpect(view().name(is("login/login")));
    }
}
