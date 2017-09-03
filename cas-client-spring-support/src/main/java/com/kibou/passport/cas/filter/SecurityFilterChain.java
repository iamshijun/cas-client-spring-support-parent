package com.kibou.passport.cas.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import com.kibou.passport.util.PatternMatcher;
import com.kibou.passport.util.ServletPathMatcher;


public class SecurityFilterChain implements Filter, ApplicationListener<ContextRefreshedEvent> {

	private List<CasFilter> casFilters;
	
	private List<AuthorizationFilter> authorizationFilters;
	
	private List<Filter> mergeFilters;
	
	private String[] ignorePatternArray;
	
	private PatternMatcher patternMatcher = ServletPathMatcher.getInstance();
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	List<Filter> getMergeFilters() {
		return mergeFilters;
	}
	
	public void setCasFilters(List<CasFilter> casFilters) {
		this.casFilters = casFilters;
	}
	
	public void setAuthorizationFilters(List<AuthorizationFilter> authorizationFilters) {
		this.authorizationFilters = authorizationFilters;
	}
	
	public SecurityFilterChain(){
	}
	
	public SecurityFilterChain(List<CasFilter> casFilters,List<AuthorizationFilter> authorizationFilters){
		//System.out.println("SecurityFilterChain.SecurityFilterChain(casFilters,authorizationFilters)");
		this.casFilters = casFilters;
		this.authorizationFilters = authorizationFilters;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		initFilters(applicationContext);
	}

	private void initFilters(ApplicationContext applicationContext) {
		
		if(casFilters == null){
			//detectAllCasFilters
			Map<String, CasFilter> casFiltersMap = applicationContext.getBeansOfType(CasFilter.class);//spring给我们返回的就是一个LinkedHashMap排过的?
			if(!casFiltersMap.isEmpty()){
				casFilters = new ArrayList<>(casFiltersMap.size());
				casFilters.addAll(casFiltersMap.values());
			}
		}
		//Assert.isTrue(casFilters != null && !casFilters.isEmpty(),"Cannot find any CAS Filters");
		
		if(authorizationFilters == null){
			//??oh no! sort
			Map<String, AuthorizationFilter> authorizationFiltersMap = applicationContext.getBeansOfType(AuthorizationFilter.class);
			if(!authorizationFiltersMap.isEmpty()){
				authorizationFilters = new ArrayList<>(authorizationFiltersMap.size());
				authorizationFilters.addAll(authorizationFiltersMap.values());
			}
		}
		
		mergeFilters = new ArrayList<>();
		
		mergeFilters.addAll(casFilters);
		
		if(authorizationFilters != null && !authorizationFilters.isEmpty()){
			mergeFilters.addAll(authorizationFilters);
			
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void init(FilterConfig filterConfig) throws ServletException {
		
		/*for(Filter filter : casFilters){
			filter.init(filterConfig);
		}*/
		
		//ps:init方法的执行在创建当前bean周期(lifecycle)之后,当前逻辑是web.xml中的配置优先于在spring容器中的配置
		String ignorePattern = filterConfig.getInitParameter("ignorePattern");
		if(!StringUtils.isEmpty(ignorePattern)) {
			this.setIgnorePattern(ignorePattern);;
		}
		
		String ignorePatternMatcherType = filterConfig.getInitParameter("ignorePatternMatcherType");
		if(!StringUtils.isEmpty(ignorePatternMatcherType)) {
			try {
				
				Class<?> matcherClazz = Class.forName(ignorePatternMatcherType);
				if(PatternMatcher.class.isAssignableFrom(matcherClazz)){
					this.setIgnorePatternMatcherType((Class)matcherClazz);
				}else {
					logger.warn("Ignore type :" + ignorePatternMatcherType + "cause it's not a PatternMatcher type");
				}
			} catch (ClassNotFoundException e) {
				logger.warn("Cannot find the PatternMatcher specified with 'ignorePatternMatcherType' in web.xml");
			}
		}
	}
	
	public void setIgnorePatternMatcherType(Class<? extends PatternMatcher> ignorePatternMatcherType) {
		if(ignorePatternMatcherType == null)
			return;
		try {
			patternMatcher = BeanUtils.instantiate(ignorePatternMatcherType);
		} catch (BeanInstantiationException e) {
			logger.warn("Instantiatiate type [" + ignorePatternMatcherType + "] failed, "
					+ "cause : '" + e.getMessage() + "', "
					+ "default PatternMatcher will be used");
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		if(isRequestExcluded((HttpServletRequest)request)) {
			chain.doFilter(request, response);
			return;
		}
		
		VirtualFilterChain vc = new VirtualFilterChain(mergeFilters, chain);
		vc.doFilter(request, response);
	}

	@Override
	public void destroy() {
		for (int i = this.casFilters.size(); i-- > 0;) {
			Filter filter = this.casFilters.get(i);
			filter.destroy();
		}
	}
	
	private boolean isRequestExcluded(HttpServletRequest request) {
		if(ignorePatternArray == null || patternMatcher == null)
			return false;
		
//		String path = request.getRequestURI() - request.getContextPath();
		String path = request.getServletPath();
		for(String pattern : ignorePatternArray) {
			if(ServletPathMatcher.getInstance().matches(pattern, path))
				return true;
		}
		return false;
	}
	
	public void setPatternMatcher(PatternMatcher patternMatcher) {
		this.patternMatcher = patternMatcher;
	}
	
	public void setIgnorePatternArray(String[] ignorePatternArray) {
		this.ignorePatternArray = ignorePatternArray;
	}
	
	public void setIgnorePattern(String ignorePattern) {
		if(!StringUtils.isEmpty(ignorePattern))
			this.ignorePatternArray = StringUtils.tokenizeToStringArray(ignorePattern, ",| ", true, true);
	}
}

