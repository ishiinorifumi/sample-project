package jp.co.disney.spplogin.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import jp.co.disney.spplogin.Application;
import jp.co.disney.spplogin.vo.SppMemberDetails;

//@Ignore("動作確認するときは@Ignoreをコメントアウトしてください。コミットするときは必ず戻してください。")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("unit")
public class CoreWebApiService動作確認用テストクラス {

	@Autowired
    private CoreWebApiService coreWebApiService;
	
	/**
	 * COR-901 認証認可
	 * @throws Exception
	 */
	@Test
	public void authorize() throws Exception {
		String android = "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
		String iphone = "Mozilla /5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B5110e Safari/601.1";
		
		String dspp = "m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU/tMLA=";
		
		coreWebApiService.authorize("hirofumi.kai.0016@ctc-g.co.jp", "test123", android, dspp);
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
		details.setEmailAddress("seiji.takahashi.900@ctc-g.co.jp");
		details.setPassword("abcABC123");
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
	
}
