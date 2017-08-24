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


public abstract class AbstractCompositeCasFilter<T extends Filter> implements Filter{

	protected FilterConfig filterConfig;
	
	//private Class<T> targetType;
	
	private ConcurrentMap<String,T> cacheFilterMap = new ConcurrentHashMap<>();
	
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException{
		
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		doInternalFilter(request,response,filterChain);
	}
	
	protected abstract void doInternalFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException;

	protected abstract T createInnerFilter(FilterConfig config,ServletRequest request) throws ServletException;
	
	protected T getInnerFilter(ServletRequest request,String key) throws ServletException{
		T innerFilter = cacheFilterMap.get(key);
		
		if(innerFilter == null){
			cacheFilterMap.putIfAbsent(key, createInnerFilter(filterConfig, request));
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
