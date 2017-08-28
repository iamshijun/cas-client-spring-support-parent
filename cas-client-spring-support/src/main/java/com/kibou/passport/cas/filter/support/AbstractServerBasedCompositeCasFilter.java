package com.kibou.passport.cas.filter.support;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kibou.passport.util.WebUtils;

/**
 * 使用serverName(e.g http://www.foo.com:8080)作为AbstractCompositeCasFilter中cacheFilterMap中的 key 的实现
 * 
 * @author aimysaber@gmail.com
 *
 * @param <T>
 */
public abstract class AbstractServerBasedCompositeCasFilter<T extends Filter> extends AbstractCompositeCasFilter<String, T> {

	@Override
	protected void doInternalFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		this.getInnerFilter(request, WebUtils.getBasePath(request))
				.doFilter(request, response, filterChain);

	}

}
