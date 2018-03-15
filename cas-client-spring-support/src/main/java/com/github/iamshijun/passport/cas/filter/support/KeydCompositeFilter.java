package com.github.iamshijun.passport.cas.filter.support;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Filter组合基础类 根据不同的key选择不同的Filter来处理后续的请求 see: {@link #cacheFilterMap}
 *
 * @param <T>
 * @author aimysaber@gmail.com
 */
public abstract class KeydCompositeFilter<K, T extends Filter> implements Filter {

    protected FilterConfig filterConfig;

    private final ConcurrentMap<K, T> cacheFilterMap = new ConcurrentHashMap<>();

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        doInternalFilter(
                getInnerFilter(request, getKey(request)),
                request, response, filterChain);
    }

    protected abstract K getKey(HttpServletRequest request);

    protected void doInternalFilter(T innerFilter, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        innerFilter.doFilter(request, response, filterChain);
    }

    protected abstract T createInnerFilter(K key, FilterConfig config, HttpServletRequest request) throws ServletException;

    protected T getInnerFilter(HttpServletRequest request, K key) throws ServletException {
        T innerFilter = cacheFilterMap.get(key);

        if (innerFilter == null) {
            cacheFilterMap.putIfAbsent(key, createInnerFilter(key, filterConfig, request));
            innerFilter = cacheFilterMap.get(key);
        }
        return innerFilter;
    }

    @Override
    public final void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

        initInternal(filterConfig);
    }

    protected void initInternal(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
        if (!cacheFilterMap.isEmpty()) {
            for (T filter : cacheFilterMap.values()) {
                filter.destroy();
            }
        }
    }

}
