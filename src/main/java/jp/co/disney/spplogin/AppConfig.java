package jp.co.disney.spplogin;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;

import jp.co.disney.spplogin.interceptor.MaintenanceInterceptor;
import jp.co.disney.spplogin.interceptor.UserAgentInterceptor;
import jp.co.disney.spplogin.web.model.Guest;

@Configuration
public class AppConfig {

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
    Guest guest() {
    	return new Guest();
    }
}
