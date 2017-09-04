package com.kibou.passport.cas.filter.support;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.jasig.cas.client.authentication.AuthenticationFilter;

/**
 * @author aimysaber@gmail.com
 *
 */
public class AuthenticationFilterSupport extends AuthenticationFilter {
	
	private String serverName;
	
	public AuthenticationFilterSupport(String serverName,FilterConfig filterConfig) throws ServletException {
		this.serverName = serverName;
	}

	@Override
	protected void initInternal(FilterConfig filterConfig) throws ServletException {
		super.initInternal(filterConfig);
		setServerName(serverName);
	}
}