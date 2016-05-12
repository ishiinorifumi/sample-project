package jp.co.disney.spplogin.helper;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.net.URI;

import org.junit.BeforeClass;
import org.junit.Test;

public class URLDecodeHelperTest {

	private static URI url;
	
	@BeforeClass
	public static void setUp() throws Exception {
		url = new URI("http://dev2.ssopen.disney.co.jp/auidauth/SessionKeyInfoUpd/?access_token=QrS8-2zCE_2ZUhvDlA3_i7RPGCopMpEr9_XgvX9mi-_5brRKk-VUR5wrSeIHqDwe3Szd0dhjDsUY5mMpfXNIDq4YQcLZmqCnDS57fqLw35XXfUk1DlEficvDNyRPRU9QItmj-NlKiZjNsKydaJiRKj8LGHuhyTWUXhx-QbASHVD-aMl1f9l0ifAVlqLkpRUCGDMhvd701MOsIENokLBCQ1YnTGq39V3QWENSSrRX4U_3qLfxespDqoXkRnHzX3qZlvIkEIx52UytUY2E-JCEus3okBcXyrjfLCv1KHZldwevQJteCaaxR1CW3BovrYKnmzOJP2aPezkBLQuVkPt_JQ&did_token=eyJhY2Nlc3NfdG9rZW4iOiJjbktPbUtkWVo3RllyVjcwdTdfUXZ3IiwiY2xpZW50SWQiOiJXREktSlAuSkEuU1BQLkdDLVNUQUdFIiwicmVmcmVzaF90b2tlbiI6InltRTVuN2pmUVkyWl9GN25Zdm5hNmciLCJzd2lkIjoiNDlFMkY1MUUtQ0ExQS00RDNCLTkyQUMtQUM1MEFEQzIwNTE3IiwidHRsIjo3MTk5fQ&id_token=eyJhbGciOiJSUzI1NiIsICJ0eXAiOiJKV1QifQ.eyJhcHAiOm51bGwsImF0X2hhc2giOiJRclM4LTJ6Q0VfMlpVaHZEIiwiYXVkIjoiZHNoYXJfYUw3Zm9SMnlFbiIsImJkIjpudWxsLCJjYXJyaWVyIjpudWxsLCJkdmlkIjpudWxsLCJleHAiOjE0NjU5NjgxMTQsImlhdCI6MTQ2Mjk0NDExNCwiaWNjaWQiOm51bGwsImltZWkiOm51bGwsImlzcyI6Imh0dHBzOi8vc3NvcGVuLmRpc25leS5jby5qcC8iLCJtb2RlbCI6ImlQaG9uZSIsIm5vbmNlIjoibi0wUzZfV3pBMk1qIiwib3BlIjowLCJvcyI6Ik9TIDlfMSIsInNwcGlkIjoiMDI0MDAwMDAyIiwic3VpZCI6bnVsbCwic3dpZCI6IjQ5RTJGNTFFLUNBMUEtNEQzQi05MkFDLUFDNTBBREMyMDUxNyIsInVzZXJfaWQiOiIwMjQwMDAwMDIiLCJ1dWlkIjpudWxsfQ.C-gHYJ3X7kjqxCFLFxCZDIKZOSIAZfcfq_pRWMEXuHrWgK3PzW74_hVV2oEG9um7Q-_0MGeInA5Uq6oQVD0xDQ&description=eyJlbWFpbF9hZGRyZXNzX2V4aXN0IjpmYWxzZSwiaXNfYm91bmNlZCI6ZmFsc2UsImlzX2luY29tcGxldGUiOmZhbHNlLCJpc19teW1lbnVfaW1vZGVfbGVmdCI6ZmFsc2UsImlzX215bWVudV9sZWZ0IjpmYWxzZSwibG9naW4iOiJzcHAzIiwibG9naW5fc3RhdHVzIjoiTUFSS0VUSU5HX1JFUVVJUkVEX0ZPQiJ9&state=m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU%2FtMLA%3D&token_type=Bearer");
	}
	
	@Test
	public void getQueryValueメソッドのテスト() throws Exception {
		final URLDecodeHelper helper = new URLDecodeHelper(url);
		
		assertThat(helper.getQueryValue("token_type"), is("Bearer"));
	}
	
	@Test
	public void getQueryValueWithUrlDecodeメソッドのテスト() throws Exception {
		final URLDecodeHelper helper = new URLDecodeHelper(url);
		
		assertThat(helper.getQueryValueWithUrlDecode("description"), is("{\"email_address_exist\":false,\"is_bounced\":false,\"is_incomplete\":false,\"is_mymenu_imode_left\":false,\"is_mymenu_left\":false,\"login\":\"spp3\",\"login_status\":\"MARKETING_REQUIRED_FOB\"}"));
	}
	
}
