package jp.co.disney.spplogin.service;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import jp.co.disney.spplogin.Application;
import jp.co.disney.spplogin.vo.DidMemberDetails;
import jp.co.disney.spplogin.vo.SppMemberDetails;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("unit")
public class CoreWebApiServiceTest {

	private static final String USER_AGENT = "Mozilla /5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B5110e Safari/601.1"; 
	
	@Rule
    public ExpectedException expectedException = ExpectedException.none();

	@Rule
    public final MockitoRule rule = MockitoJUnit.rule();
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	@Autowired
    private CoreWebApiService coreWebApiService;
	
	@SuppressWarnings("unchecked")
	@Test
	public void authorizeメソッド_正常系() throws Exception {
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI("http://dev2.ssopen.disney.co.jp/auidauth/SessionKeyInfoUpd/?access_token=QrS8-2zCE_2ZUhvDlA3_i7RPGCopMpEr9_XgvX9mi-_5brRKk-VUR5wrSeIHqDweJ70woENHNICiXWrMv0v9Oa4YQcLZmqCnDS57fqLw35XXfUk1DlEficvDNyRPRU9QItmj-NlKiZjNsKydaJiRKj8LGHuhyTWUXhx-QbASHVD-aMl1f9l0ifAVlqLkpRUCGDMhvd701MOsIENokLBCQ1YnTGq39V3QWENSSrRX4U_3qLfxespDqoXkRnHzX3qZlvIkEIx52UytUY2E-JCEus3okBcXyrjfLCv1KHZldwevQJteCaaxR1CW3BovrYKnmzOJP2aPezkBLQuVkPt_JQ&did_token=eyJhY2Nlc3NfdG9rZW4iOiJpS2R0aTBKMERDcy02UmpMaGpzOW9RIiwiY2xpZW50SWQiOiJXREktSlAuSkEuU1BQLkdDLVNUQUdFIiwicmVmcmVzaF90b2tlbiI6IlV1akNRWldXTjR6Yzh4QUVKcFVWQ2ciLCJzd2lkIjoiNDlFMkY1MUUtQ0ExQS00RDNCLTkyQUMtQUM1MEFEQzIwNTE3IiwidHRsIjo3MjAwfQ&id_token=eyJhbGciOiJSUzI1NiIsICJ0eXAiOiJKV1QifQ.eyJhcHAiOm51bGwsImF0X2hhc2giOiJRclM4LTJ6Q0VfMlpVaHZEIiwiYXVkIjoiZHNoYXJfYUw3Zm9SMnlFbiIsImJkIjpudWxsLCJjYXJyaWVyIjpudWxsLCJkdmlkIjpudWxsLCJleHAiOjE0NjU5ODAwNDIsImlhdCI6MTQ2Mjk1NjA0MiwiaWNjaWQiOm51bGwsImltZWkiOm51bGwsImlzcyI6Imh0dHBzOi8vc3NvcGVuLmRpc25leS5jby5qcC8iLCJtb2RlbCI6ImlQaG9uZSIsIm5vbmNlIjoibi0wUzZfV3pBMk1qIiwib3BlIjowLCJvcyI6Ik9TIDlfMSIsInNwcGlkIjoiMDI0MDAwMDAyIiwic3VpZCI6bnVsbCwic3dpZCI6IjQ5RTJGNTFFLUNBMUEtNEQzQi05MkFDLUFDNTBBREMyMDUxNyIsInVzZXJfaWQiOiIwMjQwMDAwMDIiLCJ1dWlkIjpudWxsfQ.UJm5UEasRLKfnW6Ew5iBBtlVyW2FaDm11YCoKsF45mieqRBYA5EIJJPX2V-ErZQVXE8DLrGXKdr4YQJIqlTJZA&description=eyJlbWFpbF9hZGRyZXNzX2V4aXN0IjpmYWxzZSwiaXNfYm91bmNlZCI6ZmFsc2UsImlzX2luY29tcGxldGUiOmZhbHNlLCJpc19teW1lbnVfaW1vZGVfbGVmdCI6ZmFsc2UsImlzX215bWVudV9sZWZ0IjpmYWxzZSwibG9naW4iOiJzcHAzIiwibG9naW5fc3RhdHVzIjoiTUFSS0VUSU5HX1JFUVVJUkVEX0ZPQiJ9&state=m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU%2FtMLA%3D&token_type=Bearer"));
        HttpStatus status = HttpStatus.FOUND;
        ResponseEntity<String>  response = new ResponseEntity<>(null, headers, status);
        
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenReturn(response);
        
		final ResponseEntity<String> res = coreWebApiService.authorize("hirofumi.kai.0016@ctc-g.co.jp", "test123", USER_AGENT, "dspp");
		
		assertThat(res.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(res.getHeaders().getLocation(), is(notNullValue()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getDidInformationメソッド_正常系() throws Exception {
		HttpStatus status = HttpStatus.OK;
        final String responseBody = "{\"did_member_details\":{\"address1\":null,\"address2\":null,\"age_band\":\"ADULT\",\"date_of_birth\":\"1990-12-30\",\"email_activation\":false,\"email_address\":\"hirofumi.kai.0032@ctc-g.co.jp\",\"first_name_kanji\":null,\"fob\":true,\"gender\":\"M\",\"guardian_email_address\":null,\"last_name_kanji\":null,\"legal_pp\":true,\"legal_tou\":true,\"login_status\":{\"is_authorized\":true,\"status\":[\"MARKETING_REQUIRED_FOB\"]},\"member_name\":\"_atgn_muwbjg_\",\"name_kana\":null,\"password\":null,\"phone_number\":null,\"postal_code\":\"1050001\",\"prefecture_code\":\"13\",\"swid\":\"8F0BC929-DA93-420A-87ED-BDD716538481\"},\"did_token\":{\"token\":\"eyJhY2Nlc3NfdG9rZW4iOiJHeHdKdjRJWjg1cUtYYTZ5dEZ4MHNBIiwiY2xpZW50SWQiOiJXREktSlAuSkEuU1BQLkdDLVNUQUdFIiwicmVmcmVzaF90b2tlbiI6IkRFeWxPNHotTVV3MWR1c2x5aXU1VWciLCJzd2lkIjoiOEYwQkM5MjktREE5My00MjBBLTg3RUQtQkRENzE2NTM4NDgxIiwidHRsIjo3MDI0fQ\"},\"status\":\"Success\"}";
        ResponseEntity<String>  response = new ResponseEntity<>(responseBody, null, status);
        
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenReturn(response);
        
        final DidMemberDetails res = coreWebApiService.getDidInformation("didToken");
        
        assertThat(res, is(notNullValue()));
        assertThat(res.getDateOfBirth(), is("1990-12-30"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getDidInformationメソッド_異常系() throws Exception {
		
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("DID会員情報照会(COR-112)呼び出し時にエラーが発生しました。");
        
		HttpStatus status = HttpStatus.BAD_REQUEST;
        final String responseBody = "{\"error\":{\"code\":\"010107\",\"message\":\"Bad Request\",\"spp_message\":\"DIDトークン有効期限切れ\",\"status\":\"400\"},\"status\":\"Error\"}{\"error\":{\"code\":\"010107\",\"message\":\"Bad Request\",\"spp_message\":\"DIDトークン有効期限切れ\",\"status\":\"400\"},\"status\":\"Error\"}";
        ResponseEntity<String>  response = new ResponseEntity<>(responseBody, null, status);
        
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenReturn(response);
        
        coreWebApiService.getDidInformation("didToken");

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void registerSppMemberメソッド_正常系() throws Exception {
		HttpStatus status = HttpStatus.CREATED;
        final String responseBody = "{\"spp_member_details\":{\"address1\":null,\"address2\":null,\"address3\":null,\"age_band\":null,\"date_of_birth\":\"1950-01-01\",\"email_activation\":null,\"email_address\":\"seiji.takahashi.902@ctc-g.co.jp\",\"first_name_kanji\":null,\"fob\":true,\"gender\":\"M\",\"guardian_email_address\":null,\"is_premium_member\":false,\"last_name_kanji\":null,\"legal_pp\":true,\"legal_tou\":true,\"login_status\":null,\"member_name\":\"_atgn_muwbjg_\",\"member_status\":null,\"name_kana\":null,\"password\":\"test123\",\"phone_number\":null,\"postal_code\":null,\"prefecture_code\":\"13\",\"registration_required\":null,\"spp_exid\":null,\"spp_id\":null,\"swid\":null,\"xmid\":null},\"status\":\"Success\"}";
        ResponseEntity<String>  response = new ResponseEntity<>(responseBody, null, status);
        
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenReturn(response);
        
        final SppMemberDetails detail = new SppMemberDetails();
        final SppMemberDetails res = coreWebApiService.registerSppMember(detail, true, true, null);
        
        assertThat(res, is(notNullValue()));
        assertThat(res.getMemberName(), is("_atgn_muwbjg_"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void registerSppMemberメソッド_正常系_DIDログイン() throws Exception {
		HttpStatus status = HttpStatus.CREATED;
        final String responseBody = "{\"spp_member_details\":{\"address1\":null,\"address2\":null,\"address3\":null,\"age_band\":null,\"date_of_birth\":\"1950-01-01\",\"email_activation\":null,\"email_address\":\"seiji.takahashi.902@ctc-g.co.jp\",\"first_name_kanji\":null,\"fob\":true,\"gender\":\"M\",\"guardian_email_address\":null,\"is_premium_member\":false,\"last_name_kanji\":null,\"legal_pp\":true,\"legal_tou\":true,\"login_status\":null,\"member_name\":\"_atgn_muwbjg_\",\"member_status\":null,\"name_kana\":null,\"password\":\"test123\",\"phone_number\":null,\"postal_code\":null,\"prefecture_code\":\"13\",\"registration_required\":null,\"spp_exid\":null,\"spp_id\":null,\"swid\":null,\"xmid\":null},\"status\":\"Success\"}";
        ResponseEntity<String>  response = new ResponseEntity<>(responseBody, null, status);
        
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenReturn(response);
        
        final SppMemberDetails detail = new SppMemberDetails();
        final SppMemberDetails res = coreWebApiService.registerSppMember(detail, true, false, "did_token_xxx");
        
        assertThat(res, is(notNullValue()));
        assertThat(res.getMemberName(), is("_atgn_muwbjg_"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void registerSppMemberメソッド_異常系() throws Exception {
		
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("SPP会員新規登録(COR-001)呼び出し時にエラーが発生しました。");
        
		HttpStatus status = HttpStatus.BAD_REQUEST;
        final String responseBody = "{\"error\":{\"code\":\"010993\",\"message\":\"Bad Request\",\"spp_message\":\"入力パラメータ不正 : prefectureCode = null\",\"status\":\"400\"},\"status\":\"Error\"}";
        ResponseEntity<String>  response = new ResponseEntity<>(responseBody, null, status);
        
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenReturn(response);
        
        final SppMemberDetails detail = new SppMemberDetails();
        coreWebApiService.registerSppMember(detail, true, true, null);

	}
}
