package com.kibou.passport.cas.filter.support;

import java.io.IOException;
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
import org.jasig.cas.client.validation.AssertionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Base64Utils;

import com.kibou.passport.cas.cache.TicketStorage;
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
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String cookieName = "MUSS";
	
	private boolean preFilterSwitchOn = true;//on
	
	private TicketStorage ticketStorage;//
	
	//private CipherExecutor(cas-server)

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}
	
	public void setPreFilterSwitchOn(boolean preFilterSwitchOn) {
		this.preFilterSwitchOn = preFilterSwitchOn;
	}
	
	private boolean isPreFilterEnabled() {
		if(preFilterSwitchOn) {
			return ticketStorage != null;
		}
		return false;
	}

	@Autowired (required=false)
	@Qualifier("ticketStorage")
	public void setTicketStorage(TicketStorage ticketStorage) {
		this.ticketStorage = ticketStorage;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		if(!isPreFilterEnabled()) {
			chain.doFilter(req, resp);
			return;
		}

		HttpServletRequest request = (HttpServletRequest) req;
		final Assertion assertion = CasClientUtils.getAssertion(request);
		
		if (assertion == null) {
			logger.debug("Request URL : " + request.getRequestURL());
			Cookie cookie = WebUtils.getCookie(request, cookieName);
			if (cookie != null) {
				//1. get MUSS and  validate(TODO) ...
				String cipher = cookie.getValue();
				
				String principal = ticketStorage.get(cipher);
				// 2. Create Assertion instance and add to session/request's attribute
				if (principal != null) {
					createAssertion(request, principal);
				}
			}
		}
		
		chain.doFilter(req, resp);
		return;
	}

	private void createAssertion(HttpServletRequest request, String principal) {
		
		logger.debug("=====" + "Found cipher and create a artifact assertion object" + "=====");
		
		// <= fixme
		AssertionImpl assertion = new AssertionImpl(principal);
		
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
			//自定义的客户端用的 ticket/token/cipher 密串
			String cipher = Base64Utils.encodeToString(UUID.randomUUID().toString().getBytes());
			ticketStorage.put(cipher, assertion.getPrincipal().getName());//上面的createAssertion需要配合 知道这里存储的对象类型! 
			
			Cookie cookie = new Cookie(cookieName, cipher);
			cookie.setHttpOnly(true);
			//cookie.setDomain(domain);
			cookie.setPath(request.getContextPath() + "/");
			//cookie.setSecure(true);
			response.addCookie(cookie);
		}
		
	}
	
	@Override
	public void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
		
	}

}
