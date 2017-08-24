package com.kibou.passport.cas.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;

import com.kibou.passport.cas.filter.CompositeAuthenticationFilter.AuthenticationFilterSupport;
import com.kibou.passport.cas.filter.support.AbstractCompositeCasFilter;
import com.kibou.passport.util.WebUtils;

public class CompositeAuthenticationFilter extends  AbstractCompositeCasFilter<AuthenticationFilterSupport> 
	/*extends org.jasig.cas.client.util.AbstractConfigurationFilter */{

	public CompositeAuthenticationFilter(){
	}
	
	@Override
	public void doInternalFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		//System.out.println("RequestURI : " + request.getRequestURI());
		
		final HttpSession session = request.getSession(false);
		final Assertion assertion = session != null ? (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : null;
		//如果一个service是有load balance为多个应用做的负载 有可能某些请求会跑到其他的机器上.
		//session同步或者是或者说Assertion的存储和获取需要改造. session同步的话对应用改造是最小的.
		
		if(assertion != null){
			//System.out.println(request.getRequestedSessionId());
			filterChain.doFilter(request, response);
			return;
		}
			
		this.getInnerFilter(request, WebUtils.getBasePath(request))
				.doFilter(request, response, filterChain);
	}

	@Override
	protected AuthenticationFilterSupport createInnerFilter(FilterConfig config, ServletRequest request) throws ServletException {
		return new AuthenticationFilterSupport(WebUtils.getBasePath((HttpServletRequest)request), filterConfig);
	}
	
	
	class AuthenticationFilterSupport extends AuthenticationFilter{
		final String serverName;
		public AuthenticationFilterSupport(String serverName,FilterConfig filterConfig) throws ServletException{
			this.serverName = Objects.requireNonNull(serverName);
			super.init(filterConfig);//-> 调用 init()
		}
		@Override //父类的init重写 父类会在init方法时开始检测serverName的设置.因为filterConfig中的配置是serverNames而不是serverName 这里显示设置filter里的serverName
		public void init() {
			super.setServerName(serverName);
			super.init();
		}
	}
}
 