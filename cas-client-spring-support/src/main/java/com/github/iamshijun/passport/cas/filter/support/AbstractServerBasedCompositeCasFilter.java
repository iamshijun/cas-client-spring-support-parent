package com.github.iamshijun.passport.cas.filter.support;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import com.github.iamshijun.passport.util.WebUtils;

/**
 * 使用serverName(e.g http://www.foo.com:8080)作为AbstractCompositeCasFilter中cacheFilterMap中的 key 的实现
 * 
 * @author aimysaber@gmail.com
 *
 * @param <T>
 */
public abstract class AbstractServerBasedCompositeCasFilter<T extends Filter> extends KeydCompositeFilter<String, T> {

	protected String getKey(HttpServletRequest request){
		return WebUtils.getBasePath(request);
	}

}
