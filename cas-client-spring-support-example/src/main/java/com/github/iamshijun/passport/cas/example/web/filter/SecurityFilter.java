package com.github.iamshijun.passport.cas.example.web.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.validation.Assertion;

import com.github.iamshijun.passport.util.CasClientUtils;

/**
 * @author aimysaber@gmail.com
 *
 */
public class SecurityFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("----------SecurityFilter.init()--------");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
//		Assertion assertion = AssertionHolder.getAssertion();
		Assertion assertion = CasClientUtils.getAssertion((HttpServletRequest)req);
		
		if(assertion != null) {
			System.out.println("===========" + assertion.getPrincipal() + "===========");
			
			Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
			System.out.println(attributes);
		}
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {

	}

}
