package jp.co.disney.spplogin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * メンテナンス中判定を行い、メンテナンスページへリダイレクトするインターセプター
 */
@Slf4j
public class MaintenanceInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (isMaintenance() &&
				!request.getRequestURI().equals(request.getContextPath() + "/maintenance")) {
			response.sendRedirect(request.getContextPath() + "/maintenance");
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * メンテナンス判定
	 * @return
	 */
	private boolean isMaintenance() {
		// TODO
		return false;
	}

}
