package com.kibou.passport.cas.filter.support;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kibou.passport.util.CasClientUtils;


/**
 * Filter组合基础类 根据不同的key选择不同的Filter来处理后续的请求 see: {@link #cacheFilterMap}
 * @author aimysaber@gmail.com
 *
 * @param <T>
 */
public abstract class AbstractCompositeCasFilter<K,T extends Filter> implements Filter{

	protected FilterConfig filterConfig;
	
	private final ConcurrentMap<K,T> cacheFilterMap = new ConcurrentHashMap<>();
	
	private boolean goThroughIfAssertionFound = true;
	
	public void setGoThroughIfAssertionFound(boolean goThroughIfAssertionFound) {
		this.goThroughIfAssertionFound = goThroughIfAssertionFound;
	}
	
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException{
		
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		if(goThroughIfAssertionFound && CasClientUtils.getAssertion(request) != null){
			// System.out.println(request.getRequestedSessionId());
			filterChain.doFilter(request, response);
			return;
		}
		
		doInternalFilter(request,response,filterChain);
	}
	
	protected abstract void doInternalFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException;

	protected abstract T createInnerFilter(K key,FilterConfig config,ServletRequest request) throws ServletException;
	
	protected T getInnerFilter(ServletRequest request,K key) throws ServletException{
		T innerFilter = cacheFilterMap.get(key);
		
		if(innerFilter == null){
			cacheFilterMap.putIfAbsent(key, createInnerFilter(key, filterConfig, request));
			innerFilter = cacheFilterMap.get(key);
		}
		return innerFilter;
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		
		initInternal(filterConfig);
	}
	
	protected void initInternal(FilterConfig filterConfig) throws ServletException{
	}
	
	@Override
	public void destroy() {
		if(!cacheFilterMap.isEmpty()){
			for(T filter : cacheFilterMap.values()){
				filter.destroy();
			}
		}
	}

}
