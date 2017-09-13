package com.github.iamshijun.passport.cas.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

//参考 ServletWrappingController
/**
 * @author aimysaber@gmail.com
 *
 */
public class FilterWrapperBean
		implements Filter, ServletContextAware, BeanNameAware, InitializingBean, DisposableBean {

	/**
	 * 封装的filter实例,如果没有指定需要指定filterClass
	 */
	private Filter filterInstance;

	/**
	 * 封装的filter实例使用的class,如果没有指定需要指定filterInstance
	 */
	private Class<? extends Filter> filterClass;
	
	private String beanName;

	/**
	 * 内部封装filter的名称
	 */
	private String filterName;
	
	private String initParamPrefix = "passport"; 
	
	/**
	 * 是否使用前缀匹配 - <br>
	 * 如果启用会从initParameters中筛选出 {@link #initParamPrefix} + "." + {@link #filterName } + "." 为前缀的属性
	 * 作为filter的config,其余的将会被忽略. <br>
	 * 如果不启用,原封不动的使用传入的initParameters作为filter的config
	 */
	private boolean useParamPrefix = true;

	private Properties initParameters = new Properties();

	private ServletContext servletContext;
	
	private Properties finalFilterInitParameters;
	
	private Properties getFilterInitParameters() {
		if(finalFilterInitParameters == null) {
			if(useParamPrefix) {
				Properties properties = new Properties();
				String propertyPrefix = initParamPrefix + "." + filterName + ".";
				for(String name : initParameters.stringPropertyNames()){
					if(name.startsWith(propertyPrefix)) {
						properties.put(name.replaceFirst(propertyPrefix, ""), initParameters.get(name));
					}
				}
				finalFilterInitParameters = properties;
			}else {
				finalFilterInitParameters = initParameters;
			}
		}
		return finalFilterInitParameters;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (filterInstance == null && this.filterClass == null) {
			throw new IllegalArgumentException("'filterClass' is required");
		}
		if (this.filterName == null) {
			this.filterName = this.beanName;
		}
		
		if(filterInstance == null) {
			this.filterInstance = this.filterClass.newInstance();
		}
		this.filterInstance.init(new DelegatingFilterConfig());
	}

	class DelegatingFilterConfig implements FilterConfig {

		@Override
		public String getFilterName() {
			return FilterWrapperBean.this.getFilterName();
		}

		@Override
		public ServletContext getServletContext() {
			return FilterWrapperBean.this.getServletContext();
		}

		@Override
		public String getInitParameter(String name) {
			return FilterWrapperBean.this.getFilterInitParameters().getProperty(name);
		}

		@Override
		@SuppressWarnings("unchecked")
		public Enumeration<String> getInitParameterNames() {
			//return (Enumeration)initParameters.keys();
			return (Enumeration<String>)FilterWrapperBean.this.getFilterInitParameters().propertyNames();
		}

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		filterInstance.doFilter(request, response, chain);
	}

	@Override
	public void destroy() {
		filterInstance.destroy();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//?do what?
	}
	
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}
	
	public void setFilterInstance(Filter filterInstance) {
		this.filterInstance = filterInstance;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}
	
	public void setFilterClass(Class<? extends Filter> filterClass) {
		this.filterClass = filterClass;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String getFilterName() {
		return filterName;
	}
	
	public void setUseParamPrefix(boolean useParamPrefix) {
		this.useParamPrefix = useParamPrefix;
	}
	
	public void setInitParameters(Properties initParameters) {
		this.initParameters = initParameters;
	}
	
	public void setInitParamPrefix(String initParamPrefix) {
		this.initParamPrefix = initParamPrefix;
	}
	
	@Override
	public String toString() {
		return "(delegate)" + 
				(filterClass == null ? filterInstance.getClass() : filterClass) +
				"@" + System.identityHashCode(this);
	}
}
