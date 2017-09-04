package com.kibou.passport.cas.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author aimysaber@gmail.com
 *
 */
public class FilterDelegator implements Filter{
	
	public final Filter delegate;
	
	public FilterDelegator(Filter delegate) {
		this.delegate = delegate;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		delegate.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		delegate.doFilter(request, response, chain);
	}

	@Override
	public void destroy() {
		delegate.destroy();
	}

}
