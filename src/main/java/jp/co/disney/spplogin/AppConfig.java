package jp.co.disney.spplogin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import jp.co.disney.spplogin.interceptor.MaintenanceInterceptor;
import jp.co.disney.spplogin.interceptor.UserAgentInterceptor;

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
}
