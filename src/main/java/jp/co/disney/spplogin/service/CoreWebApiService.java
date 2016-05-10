package jp.co.disney.spplogin.service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.disney.spp.v3.core.common.exception.CoreException;
import jp.co.disney.spp.v3.core.common.util.JwtSign;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CoreWebApiService {

	private static final String REDIRECT_URI = "http://dev2.ssopen.disney.co.jp/auidauth/SessionKeyInfoUpd/";

	@Value("${spplogin.core-webapi.base-url}")
	private String baseUrl;

	@Value("${spplogin.core-webapi.cor-901.path}")
	private String path;

	@Value("${spplogin.core-webapi.cor-901.client-id}")
	private String clientId;

	@Value("${spplogin.core-webapi.cor-901.nonce}")
	private String nonce;

	@Value("${spplogin.core-webapi.cor-901.response-type}")
	private String responseType;

	@Value("${spplogin.core-webapi.cor-901.scope}")
	private String scope;

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

		} catch (JsonProcessingException | CoreException e) {
			throw new RuntimeException(e);
		}

		final URI url = UriComponentsBuilder
				.fromUriString(baseUrl)
				.path(path)
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

		log.debug("Request URL: {}", url.toString());

		final HttpHeaders headers = new HttpHeaders();
		headers.set("User-Agent", userAgent);
		final HttpEntity<?> entity = new HttpEntity<>(headers);

		final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		
		log.debug("Response Status : {}", response.getStatusCode());
		log.debug("Location : {}", response.getHeaders().getLocation());
		
		return response;
	}
}