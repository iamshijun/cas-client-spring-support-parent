package com.kibou.passport.cas.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.authentication.DelegatingAuthenticationFilter;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;

import com.kibou.passport.cas.filter.support.AbstractCompositeCasFilter;
import com.kibou.passport.util.WebUtils;

@Deprecated
public class CompositeAuthenticationFilter2 extends  AbstractCompositeCasFilter<AuthenticationFilter> {
	
	@Override
	public void doInternalFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		final HttpSession session = request.getSession(false);
		final Assertion assertion = session != null ? (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : null;
		
		if(assertion != null){
			filterChain.doFilter(request, response);
			return;
		}
			
		this.getInnerFilter(request, WebUtils.getBasePath(request))
				.doFilter(request, response, filterChain);
	}

	@Override
	protected AuthenticationFilter createInnerFilter(FilterConfig config, ServletRequest request) throws ServletException {
		
		DelegatingAuthenticationFilter authenticationFilter = new DelegatingAuthenticationFilter();
		authenticationFilter.setInnerServerName(WebUtils.getBasePath((HttpServletRequest)request));
		
		authenticationFilter.init(config);
		
		return authenticationFilter;
	}
}
 