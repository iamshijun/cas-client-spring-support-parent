package com.kibou.passport.cas.filter.support;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * 可追加配置的FilterConfig(delegate)
 * @author aimysaber@gmail.com
 *
 */
public class AppendableFilterConfig implements FilterConfig{

	private final FilterConfig delegate;

	private boolean overrideInitParam = true;
	
	private Map<String,String> appendParams;
	
	public AppendableFilterConfig(FilterConfig delegate) {
		this.delegate = delegate;
		this.appendParams = new HashMap<>();
	}
	
	
	/**
	 * 设置是否覆盖内部FilterConfig的原有配置
	 * @param overrideInitParam
	 */
	public void setOverrideInitParam(boolean overrideInitParam) {
		this.overrideInitParam = overrideInitParam;
	}
	
	public String append(String name,String value) {
		return this.appendParams.put(name, value);
	}
	
	@Override
	public String getFilterName() {
		return delegate.getFilterName();
	}

	@Override
	public ServletContext getServletContext() {
		return delegate.getServletContext();
	}

	@Override
	public String getInitParameter(String name) {
		String initParameter = delegate.getInitParameter(name);
		if(initParameter == null || (overrideInitParam && appendParams.containsKey(name))) {
			initParameter = appendParams.get(name);
		}
		return initParameter;
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		
		Enumeration<String> initParameterNames = delegate.getInitParameterNames();
		
		final Map<String,String> initParameterMap = new HashMap<>();

		while(initParameterNames.hasMoreElements()) {
			
			String name = initParameterNames.nextElement();
			
			String initParameter = delegate.getInitParameter(name);
			
			if(overrideInitParam && appendParams.containsKey(name)) {
				initParameter = appendParams.get(name);
			}
			initParameterMap.put(name, initParameter);
		}
		
		return new Enumeration<String>() {
			
			Iterator<String> nameIterator = initParameterMap.keySet().iterator();

			@Override
			public String nextElement() {
				return nameIterator.next();
			}
			
			@Override
			public boolean hasMoreElements() {
				return nameIterator.hasNext();
			}
		};
	}

}
