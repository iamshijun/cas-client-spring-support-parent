package com.github.iamshijun.passport.cas.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.validation.Assertion;

import com.github.iamshijun.passport.cas.handler.TicketValidationHandler;
import com.github.iamshijun.passport.util.CasClientUtils;
import com.github.iamshijun.passport.util.WebUtils;

//make sure you are using the CAS2.X or later
public class CasSingleLoginFilter implements Filter , TicketValidationHandler { //单设备(浏览器)登录 - 单会话!

	private boolean enable = false;
	
	private String forceLogoutUrl;
	
	private String keyPrefix = "passport:user:sessionid:";
	
	public void setEnable(boolean enabled) {
		this.enable = enabled;
	}
	
	public void setForceLogoutUrl(String forceLogoutUrl) {
		this.forceLogoutUrl = forceLogoutUrl;
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//TODO? init param with filterConfig 
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		if(enable){
			
			HttpServletRequest request = WebUtils.toHttpRequest(req);
			HttpServletResponse response = WebUtils.toHttpResponse(resp);
			
			HttpSession httpSession = request.getSession(false);
			Assertion assertion = CasClientUtils.getAssertion(request) ;
			
			if(httpSession != null && assertion != null) {
				try {
					//如果使用了session共享 , 否则使用其他分布式存储方案(由用户提供) TODO 提供接口
					/*String lastSessionID = redis.hget(keyPrefix + service,username);
					
					if(!httpSession.getId().equals(lastSessionID)) {
						if(WebUtils.isAjaxRequest(request)){
							//response.setStatus(HttpServletResponse.SC_);//???
							response.getWriter().println("forceLogout");
							response.getWriter().flush();
							return;
						}
						//request.getSession().invalidate();
						response.sendRedirect(response.encodeRedirectURL(forceLogoutUrl)) ;
						return;
					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {
		
	}
	
	@Override
	public void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response, Assertion assertion) {
		if(enable){
			/*try {
				session/redis.hset(keyPrefix + service, request.getSession().getId(), username);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}

	@Override
	public void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
	}

}
