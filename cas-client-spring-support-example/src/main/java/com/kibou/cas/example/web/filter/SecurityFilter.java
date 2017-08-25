package com.kibou.cas.example.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;

public class SecurityFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("----------SecurityFilter.init()--------");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		//HttpServletRequest request = (HttpServletRequest) req;
		//request.getSession()
		
		Assertion assertion = AssertionHolder.getAssertion();
		if(assertion != null) {
			System.out.println("===========" + assertion.getPrincipal() + "===========");
		}
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {

	}

}
