package jp.co.disney.spplogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Autowired
    HandlerInterceptor maintenanceInterceptor;
    
    @Autowired
    HandlerInterceptor userAgentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(userAgentInterceptor);
        registry.addInterceptor(maintenanceInterceptor);
    }
}
