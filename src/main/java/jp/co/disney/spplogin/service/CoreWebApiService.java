package jp.co.disney.spplogin.service;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import jp.co.disney.spp.v3.core.common.util.JwtSign;
import jp.co.disney.spplogin.vo.DidMemberDetails;
import jp.co.disney.spplogin.vo.SppMemberDetails;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CoreWebApiService {

	private static final String REDIRECT_URI = "http://dev.ssopen.disney.co.jp/auidauth/SessionKeyInfoUpd/";

	@Value("${spplogin.core-webapi.base-url}")
	private String baseUrl;

	@Value("${spplogin.core-webapi.port}")
	private String port;
	
	@Value("${spplogin.core-webapi.cor-901.path}")
	private String cor901path;

	@Value("${spplogin.core-webapi.cor-901.client-id}")
	private String clientId;

	@Value("${spplogin.core-webapi.cor-901.nonce}")
	private String nonce;

	@Value("${spplogin.core-webapi.cor-901.response-type}")
	private String responseType;

	@Value("${spplogin.core-webapi.cor-901.scope}")
	private String scope;
	
	@Value("${spplogin.core-webapi.cor-112.path}")
	private String cor112path;

	@Value("${spplogin.core-webapi.cor-001.path}")
	private String cor001path;
	
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * <pre>
	 * COR-901 認証認可要求
	 * </pre>
	 * 
	 * @param memberNameOrEmailAddr メンバー名もしくはメールアドレス
	 * @param password　パスワード
	 * @param userAgent　ユーザエージェント
	 */
	public ResponseEntity<String> authorize(String memberNameOrEmailAddr, String password, String userAgent) {

		final Map<String, String> openidRequest = new HashMap<>();
		openidRequest.put("member_name", memberNameOrEmailAddr);
		openidRequest.put("password", password);
		openidRequest.put("client_id", clientId);
		openidRequest.put("nonce", nonce);

		String jwtRequest;

		try {
			final String jsonRequest = new ObjectMapper().writeValueAsString(openidRequest);

			log.debug("JSON Request: {}", jsonRequest);

			jwtRequest = JwtSign.getInstance().sign(jsonRequest);

			log.debug("JWT Request: {}", jwtRequest);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		final URI url = UriComponentsBuilder
				.fromUriString(baseUrl)
				.path(cor901path)
				.port(port)
				.queryParam("response_type", responseType)
				.queryParam("client_id", clientId)
				.queryParam("redirect_uri", REDIRECT_URI)
				.queryParam("scope", scope)
				.queryParam("state", "m3eK3TpVRrFhKgx92Kn9TlEgdpU6uHA2LdCUbU/tMLA=")
				.queryParam("nonce", nonce)
				.queryParam("request", jwtRequest)
				.build()
				.encode()
				.toUri();

		
		log.debug("COR-901 Request URL: {}", url.toString());
		
		final HttpHeaders headers = new HttpHeaders();
		headers.set("User-Agent", userAgent);
		final HttpEntity<?> entity = new HttpEntity<>(headers);

		final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		
		log.debug("Response Status : {}", response.getStatusCode());
		log.debug("Location : {}", response.getHeaders().getLocation());
		
		return response;
	}
	
	/**
	 * <pre>
	 * COR-112 DID登録情報照会
	 * </pre>
	 * @param didToken DIDトークン
	 * @return DID会員情報詳細
	 */
	public DidMemberDetails getDidInformation(String didToken) {
		final URI url = UriComponentsBuilder
				.fromUriString(baseUrl)
				.path(cor112path)
				.port(port)
				.queryParam("did_token", didToken)
				.build()
				.encode()
				.toUri();
		
		log.debug("COR-112 Request URL : {}", url.toString());
		
		try {
			final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
			
			log.debug("Response Status : {}", response.getStatusCode());
			log.debug("Response Body : {}", response.getBody());
			
			if(response.getStatusCode().series().equals(HttpStatus.Series.CLIENT_ERROR)) {
				log.error("DID会員情報照会(COR-112)呼び出し時にエラーが発生しました。 : {}", response.getBody());
				throw new RuntimeException("DID会員情報照会(COR-112)呼び出し時にエラーが発生しました。");
			}
			
			final ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
			
			Cor112Response cor112Response =  mapper.readValue(response.getBody(), Cor112Response.class); 
			
			log.debug("COR-112 Response : {}", cor112Response);
			
			return cor112Response.getDidMemberDetails();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * <pre>
	 * COR-001 SPP会員情報新規登録
	 * </pre>
	 * @param sppMemberDetail 登録情報詳細
	 * @param actual 登録指示フラグ
	 * @param isFreshForDid DID会員登録フラグ
	 * @param didToken DID会員登録フラグがFalseの場合必須
	 */
	public SppMemberDetails registerSppMember(SppMemberDetails sppMemberDetail, boolean actual, boolean isFreshForDid, String didToken) {
		final URI url = UriComponentsBuilder
				.fromUriString(baseUrl)
				.port(port)
				.path(cor001path)
				.queryParam("actual", actual ? 1 : 0)
				.queryParam("did_token", didToken)
				.build()
				.encode()
				.toUri();
		
		log.debug("COR-001 Request URL : {}", url.toString());
		
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		Cor001Request req = new Cor001Request();
		SppMemberRegister register = new SppMemberRegister();
		register.setSppMemberDetails(sppMemberDetail);
		register.setIsFreshForDid(isFreshForDid);
		req.setSppMemberRegister(register);
		
		String requestJson;
		try {
			requestJson = mapper.writeValueAsString(req);
			log.debug("Request Body : {}", requestJson);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		final HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		
		log.debug("Response status : {}", response.getStatusCode());
		log.debug("Response Body : {}", response.getBody());
		
		if(response.getStatusCode().series().equals(HttpStatus.Series.CLIENT_ERROR)){
			log.error("SPP会員新規登録(COR-001)呼び出し時にエラーが発生しました。 : {}", response.getBody());
			throw new RuntimeException("SPP会員新規登録(COR-001)呼び出し時にエラーが発生しました。");
		}
		
		Cor001Response cor001Response = null;
		
		try {
			cor001Response = mapper.readValue(response.getBody(), Cor001Response.class);
		} catch (IOException e) {
			new RuntimeException(e);
		}

		log.debug("COR-001 Response : {}", cor001Response);
		
		return cor001Response.getSppMemberDetails();
	}
	
	/**
	 * COR-001 リクエスト
	 */
	@Data
	@ToString
	private static class Cor001Request {
		private SppMemberRegister sppMemberRegister;
		
	}
	
	/**
	 * COR-001レスポンス
	 *
	 */
	@Data
	@ToString
	private static class Cor001Response {
		private String status;
		private SppMemberDetails sppMemberDetails;
	}
	
	/**
	 * SPP会員登録要求
	 */
	@Data
	@ToString
	private static class SppMemberRegister {
		private Boolean isFreshForIur;
		private Boolean isFreshForDid;
		private SppMemberDetails sppMemberDetails;
		private String emailAddress;
		private Boolean mdmAgreement;
		private Boolean guardianActivateFlag;

	}

	/**
	 * COR-112正常レスポンス
	 */
	@Data
	@ToString
	private static class Cor112Response {
		private DidMemberDetails didMemberDetails;
	}
}