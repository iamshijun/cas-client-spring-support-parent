package com.kibou.passport.cas.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

//spring也有同名(作用一样)的类,和CompositeFilter
/**
 * @author aimysaber@gmail.com
 *
 */
public class VirtualFilterChain implements FilterChain {

	private final List<? extends Filter> filters;
	private final FilterChain originalChain;
	private int currentPosition;

	VirtualFilterChain(List<? extends Filter> filters, FilterChain originalChain) {
		this.filters = filters;
		this.originalChain = originalChain;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

		if (currentPosition == filters.size()) {
			originalChain.doFilter(request, response);
		} else {
			currentPosition++;
			Filter nextFilter = filters.get(currentPosition - 1);
			nextFilter.doFilter(request, response, this);
			
		}

	}

}
