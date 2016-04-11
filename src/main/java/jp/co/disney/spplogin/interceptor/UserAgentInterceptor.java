package jp.co.disney.spplogin.interceptor;

import is.tagomor.woothee.Classifier;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * サービス提供対象のUserAgent判定を行うインターセプター
 *
 */
@Slf4j
public class UserAgentInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
			return true;
	}
	
	private boolean isSupportModel(String userAgent) {
		Map<String, String> r = Classifier.parse(userAgent);
		
		switch(r.get("os")) {
			case "Android" :
				if(Integer.parseInt(r.get("version").substring(0, 1)) < 5
						|| !r.get("name").equals("Chrome")) {
					return false;
				}
				break;
			case "iPhone" :
				if(Integer.parseInt(r.get("version").substring(0, 1)) < 7
						|| !r.get("name").equals("Safari")) {
					return false;
				}
		}
		return true;
	}
}
