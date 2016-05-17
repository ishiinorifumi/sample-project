package jp.co.disney.spplogin;
import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;

import jp.co.disney.spplogin.exception.ApplicationErrors;
import jp.co.disney.spplogin.exception.ApplicationException;
import jp.co.disney.spplogin.interceptor.MaintenanceInterceptor;
import jp.co.disney.spplogin.interceptor.UserAgentInterceptor;
import jp.co.disney.spplogin.web.model.Guest;
import jp.co.disney.spplogin.web.model.ServiceInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AppConfig {
	@Value("${spplogin.core-webapi.proxy.enable}")
	private boolean proxyEnable;
	@Value("${spplogin.core-webapi.proxy.schema}")
	private String proxySchema;
	@Value("${spplogin.core-webapi.proxy.host}")
	private String proxyHost;
	@Value("${spplogin.core-webapi.proxy.port}")
    private int proxyPort;
	@Value("${spplogin.core-webapi.proxy.user}")
    private String proxyUser;
	@Value("${spplogin.core-webapi.proxy.password}")
    private String proxyPass;
    
	@Bean
    HandlerInterceptor maintenanceInterceptor(){
	    return new MaintenanceInterceptor();
	}

    @Bean
	HandlerInterceptor userAgentInterceptor(){
    	return new UserAgentInterceptor();
	}
    
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
    	return (container -> {
    		final ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/400");
    		final ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
    		final ErrorPage error405Page = new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/405");
    		final ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
    		container.addErrorPages(error400Page, error404Page, error405Page, error500Page);
       });
    }
    
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Guest guest() {
    	return new Guest();
    }
    
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ServiceInfo serviceInfo() {
    	return new ServiceInfo();
    }
    
    @Bean
    public RestTemplate restTemplate() {
		
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		
		if(proxyEnable) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(proxyUser, proxyPass));
			clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort, proxySchema));
			clientBuilder.setDefaultCredentialsProvider(credsProvider);
			clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
		}
		
		clientBuilder.disableCookieManagement();
		clientBuilder.disableRedirectHandling();
		
		CloseableHttpClient client = clientBuilder.build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
	    
		final RestTemplate restTemplate = new RestTemplate(factory);
		
	    restTemplate.setErrorHandler(new ResponseErrorHandler(){

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				if(HttpStatus.Series.SERVER_ERROR.equals(response.getStatusCode().series())) {
					log.error("Core WebAPI呼び出し時に予期せぬエラーが発生しました。");
					log.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
					throw new ApplicationException(ApplicationErrors.UNEXPECTED, "CoreAPIエラー");
				}
			}

			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				HttpStatus.Series series = response.getStatusCode().series();
		        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
		                || HttpStatus.Series.SERVER_ERROR.equals(series));
			}
	    	
	    });
	    
	    return restTemplate;
    }
}
