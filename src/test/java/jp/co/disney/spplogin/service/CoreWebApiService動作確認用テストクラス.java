package jp.co.disney.spplogin.service;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jp.co.disney.spplogin.Application;
import jp.co.disney.spplogin.helper.URLDecodeHelper;
import jp.co.disney.spplogin.vo.SppMemberDetails;
import lombok.extern.slf4j.Slf4j;

//@Ignore("動作確認するときは@Ignoreをコメントアウトしてください。コミットするときは必ず戻してください。")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("unit")
@Slf4j
public class CoreWebApiService動作確認用テストクラス {

	@Autowired
    private CoreWebApiService coreWebApiService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * COR-901 認証認可
	 * @throws Exception
	 */
	@Test
	public void authorize() throws Exception {
		String android = "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
		String iphone = "Mozilla /5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B5110e Safari/601.1";
		
		String dspp = "m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU/tMLA=";
		
		coreWebApiService.authorize("miushokazta@docomo.ne.jp", "test123", android, dspp);
		
	}

	/**
	 * COR-112 DID会員情報照会
	 * @throws Exception
	 */
	@Test
	public void getDidInformation() throws Exception {
		String didToken = "eyJhY2Nlc3NfdG9rZW4iOiJPRjNNOWJidFdoNW1HZmluRDNPb3ZRIiwiY2xpZW50SWQiOiJXREktSlAuSkEuU1BQLkdDLVNUQUdFIiwicmVmcmVzaF90b2tlbiI6InJrTGg0QXBIQXVsWjJWckJiT1E0WEEiLCJzd2lkIjoiOEYwQkM5MjktREE5My00MjBBLTg3RUQtQkRENzE2NTM4NDgxIiwidHRsIjo3MTk5fQ";
		//String didToken = "eyJhY2Nlc3NfdG9rZW4iOiJHeHdKdjRJWjg1cUtYYTZ5dEZ4MHNBIiwiY2xpZW50SWQiOiJXREktSlAuSkEuU1BQLkdDLVNUQUdFIiwicmVmcmVzaF90b2tlbiI6IkRFeWxPNHotTVV3MWR1c2x5aXU1VWciLCJzd2lkIjoiOEYwQkM5MjktREE5My00MjBBLTg3RUQtQkRENzE2NTM4NDgxIiwidHRsIjo3MjAwfQ";
		coreWebApiService.getDidInformation(didToken);
	}
	
	/**
	 * COR-001 SPP会員新規登録
	 * @throws Exception
	 */
	@Test
	public void registerSppMember() throws Exception {
		
		SppMemberDetails details = new SppMemberDetails();
		details.setEmailAddress("miushokazta@docomo.ne.jp");
		details.setPassword("test123");
		details.setGender("M");
		details.setDateOfBirth("1950-01-01");
		details.setFob(true);
		details.setLegalPp(true);
		details.setLegalTou(true);
		details.setPrefectureCode("13");
		coreWebApiService.registerSppMember(details, false, true, null);
	}
	
	/**
	 * COR-001 SPP会員新規登録（DIDアカウント）
	 * @throws Exception
	 */
	@Test
	public void registerSppMemberDid() throws Exception {
		
		SppMemberDetails details = new SppMemberDetails();
		details.setEmailAddress("seiji.takahashi.900@ctc-g.co.jp");
		details.setPassword("test123");
		details.setGender("M");
		details.setDateOfBirth("1950-01-01");
		details.setFob(true);
		details.setLegalPp(true);
		details.setLegalTou(true);
		details.setPrefectureCode("13");
		
		//String didToken = "eyJhY2Nlc3NfdG9rZW4iOiI0WVg2V1IwbVFPRDJSU0FwQjViRFlBIiwiY2xpZW50SWQiOiJXREktSlAuSkEuU1BQLkdDLVNUQUdFIiwicmVmcmVzaF90b2tlbiI6Il9mQzF2Qy1MSTVTd0FfQnRTd3JGdWciLCJzd2lkIjoiOEYwQkM5MjktREE5My00MjBBLTg3RUQtQkRENzE2NTM4NDgxIiwidHRsIjo3MTk5fQ";
		String didToken = "eyJhY2Nlc3NfdG9rZW4iOiI0ZGhHWEpEV1I4SUQtTVlHQXdubFVRIiwiY2xpZW50SWQiOiJXREktSlAuSkEuU1BQLkdDLVNUQUdFIiwicmVmcmVzaF90b2tlbiI6Ii1KLVZBUTlWSVVaMlo5cEVoc0hIQVEiLCJzd2lkIjoiOEYwQkM5MjktREE5My00MjBBLTg3RUQtQkRENzE2NTM4NDgxIiwidHRsIjo3MTk5fQ";
		coreWebApiService.registerSppMember(details, false, false, didToken);
	}
	
	@Test
	public void SPP会員退会() throws Exception {
		String mailAddress = "miushokazta@docomo.ne.jp";
		String password = "test123";
		String ua = "Mozilla /5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B5110e Safari/601.1";
		String dspp = "m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU/tMLA=";
		
		ResponseEntity<String> res = coreWebApiService.authorize(mailAddress, password, ua, dspp);
		
		URLDecodeHelper helper = new URLDecodeHelper(res.getHeaders().getLocation());
		final String accessToken = helper.getQueryValue("access_token");
		final String idToken = helper.getQueryValue("id_token");
		
		log.debug("access-token: {}", accessToken);
		log.debug("id-token: {}", idToken);
		
		final URI url = UriComponentsBuilder
				.fromUriString("http://localhost")
				.port(8989)
				.path("/webapi/v1/SPPMembership/me")
				.queryParam("actual", 0)
				.queryParam("id_token", idToken)
				.build()
				.encode()
				.toUri();
		
		log.debug("URL: {}", url);
		
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		final HttpEntity<?> entity = new HttpEntity<>(headers);

		final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
		
		log.debug("status: {}", response.getStatusCode());
		log.debug("response: {}", response.getBody());
	}
	
}
