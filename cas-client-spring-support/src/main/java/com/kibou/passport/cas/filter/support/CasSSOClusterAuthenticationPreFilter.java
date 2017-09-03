package com.kibou.passport.cas.filter.support;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Base64Utils;

import com.kibou.passport.cas.cache.AssertionStorage;
import com.kibou.passport.cas.handler.TicketValidationHandler;
import com.kibou.passport.util.CasClientUtils;
import com.kibou.passport.util.WebUtils;


/**
 * 在当前架构没有实现session同步的情况下使用该类来实现 一个service后面多个服务器之间的模拟登录(一台登录过 如果Load balancer转发到其他机器时会通过此filter检验出登录状态)
 * 配合自定义的TicketValidationFilter(成功登录后会在cookie存储一个标识,
 * 分布式缓存一个key/value=key同前面的cookie存储哦标识,value为Assertion的信息 用于在其他机器再将Assertion重新构造出来)
 * 
 * @author aimysaber@gmail.com
 *
 */
public class CasSSOClusterAuthenticationPreFilter implements Filter, TicketValidationHandler{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CasSSOClusterAuthenticationPreFilter.class);
	
	private String cookieName = "KUSS";
	
	private boolean switchOn = true;//on
	
	private AssertionStorage assertionStorage;//
	
	private String logoutUrl = "/logout"; 
	
	private boolean cookieSecurity = false;
	
	//private String cookiePath = "/" ;
	
	//private CipherExecutor(see : cas-server)

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@Autowired (required=false)
	@Qualifier("assertionStorage")
	public void setAssertionStorage(AssertionStorage assertionStorage) {
		this.assertionStorage = assertionStorage;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		if(!isPreFilterEnabled()) {
			chain.doFilter(req, resp);
			return;
		}
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		if(request.getRequestURL().toString().contains(logoutUrl)) {
			Cookie cookie = new Cookie(cookieName, "");
			cookie.setPath(request.getContextPath() + "/");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
		
		final Assertion assertion = CasClientUtils.getAssertion(request);
		
		if (assertion == null) {
			
			LOGGER.debug("Request URL : " + request.getRequestURL());
			
			Cookie cookie = WebUtils.getCookie(request, cookieName);
			if (cookie != null) {
				
				String cipher = cookie.getValue(); //validate(TODO) ...
				LOGGER.debug("=====Found sso cookie " + cipher + "=====");
				
				Assertion storedAssertion = assertionStorage.get(cipher);
				
				if(storedAssertion != null) {
					LOGGER.debug("=====" + "Found shared assertion object from storage" + "=====");
					
					putAssertionIntoConversation(request, storedAssertion);
				}
			}
		}
		
		chain.doFilter(req, resp);
		return;
	}

	protected void putAssertionIntoConversation(HttpServletRequest request,Assertion assertion ){
		
		//create session immediately if it is not a ajax request?
		HttpSession session = request.getSession(WebUtils.isAjaxRequest(request) ? false : true);
		if(session != null) {
			session.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION, assertion); 
		}else {
			request.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION, assertion);
		}

	}

	@Override
	public void destroy() {
		
	}
	
	@Override
	public void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response, Assertion assertion) {
		if(isPreFilterEnabled()) {
			//自定义的客户端用的 ticket/token/cipher 密串,这里没有安全问题?单纯的使用一个UUID作为key
			@SuppressWarnings("restriction")
			String cipher = new sun.misc.BASE64Encoder().encode(UUID.randomUUID().toString().getBytes());
			//String cipher = Base64Utils.encodeToString(UUID.randomUUID().toString().getBytes());
			
			assertionStorage.put(cipher, assertion);//上面的createAssertion需要配合 知道这里存储的对象类型! 
			
			Cookie cookie = new Cookie(cookieName, cipher);
			cookie.setHttpOnly(true);
			//cookie.setDomain(request.getHost());
			cookie.setPath(request.getContextPath() + "/");
			if(cookieSecurity) {
				cookie.setSecure(true);
			}
			
			response.addCookie(cookie);
			
			LOGGER.debug("add sso cookie to " + request.getContextPath());
		}
		
	}
	
	@Override
	public void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
		
	}
	
	public void setCookieSecurity(boolean cookieSecurity) {
		this.cookieSecurity = cookieSecurity;
	}
	
	//set logoutUrl pattern
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}
	
	public void setSwitchOn(boolean switchOn) {
		this.switchOn = switchOn;
	}
	
	private boolean isPreFilterEnabled() {
		if(switchOn) {
			return assertionStorage != null;
		}
		return false;
	}

}
