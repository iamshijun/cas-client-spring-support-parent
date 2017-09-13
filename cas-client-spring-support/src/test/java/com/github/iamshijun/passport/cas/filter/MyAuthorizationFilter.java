package com.github.iamshijun.passport.cas.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.github.iamshijun.passport.cas.filter.AuthorizationFilter;

/**
 * @author aimysaber@gmail.com
 *
 */
public class MyAuthorizationFilter implements AuthorizationFilter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

	}

	@Override
	public void destroy() {

	}

}
