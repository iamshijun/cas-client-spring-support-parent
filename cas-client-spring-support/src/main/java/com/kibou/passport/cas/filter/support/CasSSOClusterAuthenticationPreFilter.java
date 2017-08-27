package com.kibou.passport.cas.filter.support;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;


/**
 * 在当前架构没有实现session同步的情况下使用该类来实现 一个service后面多个服务器之间的模拟登录(一台登录过 如果Load balancer转发到其他机器时会通过此filter检验出登录状态)
 * 配合自定义的TicketValidationFilter(成功登录后会在cookie存储一个标识,
 * 分布式缓存一个key/value=key同前面的cookie存储哦标识,value为Assertion的信息 用于在其他机器再将Assertion重新构造出来)
 * @author aimysaber@gmail.com
 *
 */
public class CasSSOClusterAuthenticationPreFilter implements Filter{
	
	private String cookieName = "MUSS";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String initParamCookieName = filterConfig.getInitParameter("cookieName");
		if(initParamCookieName != null) {
			cookieName = initParamCookieName;
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		//Authentication 会通过在session中查找Assertion来判断是否登录过
		// final HttpSession session = request.getSession(false);
        //final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;

		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession(false);
		
		final Assertion assertion = session != null ? (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : null;
		
		if(assertion == null) {
			Cookie[] cookies = request.getCookies();
			if(cookies != null && cookies.length > 0) {
				for(Cookie cookie : cookies) {
					if(cookie.getName().equals(cookieName)) {
						
						//TODO(shisj)
						//1. validate MUSS & checkout from cache 
						
						//2. create Assertion and insert into session
						session = request.getSession(true);//
						session.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION,new AssertionImpl(cookie.getValue()));
						//logger.debug
						System.out.println("=====" + "Found MUSS and create a artifact assertion object" + "=====");
					}
				}
			}
		}
		
		chain.doFilter(req, resp);
		return;
	}

	@Override
	public void destroy() {
		
	}

}
