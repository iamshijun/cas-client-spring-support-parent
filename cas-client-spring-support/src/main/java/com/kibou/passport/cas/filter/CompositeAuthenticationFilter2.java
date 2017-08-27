package com.kibou.passport.cas.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.authentication.DelegatingAuthenticationFilter;

import com.kibou.passport.cas.filter.support.AbstractServerBasedCompositeCasFilter;

@Deprecated
public class CompositeAuthenticationFilter2 extends  AbstractServerBasedCompositeCasFilter<AuthenticationFilter> {
	
	@Override
	protected AuthenticationFilter createInnerFilter(String serverName, FilterConfig config, ServletRequest request) throws ServletException {
		
		DelegatingAuthenticationFilter authenticationFilter = new DelegatingAuthenticationFilter();
		authenticationFilter.setInnerServerName(serverName);
		
		authenticationFilter.init(config);
		
		return authenticationFilter;
	}
}
 