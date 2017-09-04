package com.kibou.passport.cas.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.jasig.cas.client.authentication.AuthenticationFilter;

import com.kibou.passport.cas.filter.support.AbstractServerBasedCompositeCasFilter;
import com.kibou.passport.cas.filter.support.AuthenticationFilterSupport;

/**
 * @author aimysaber@gmail.com
 */
public class CompositeAuthenticationFilter extends AbstractServerBasedCompositeCasFilter<AuthenticationFilter> 
		/* extends org.jasig.cas.client.util.AbstractConfigurationFilter */ {

	@Override
	protected AuthenticationFilter createInnerFilter(String serverName,FilterConfig config, ServletRequest request)
			throws ServletException {

		AuthenticationFilterSupport authenticationFilter = new AuthenticationFilterSupport(serverName,config);
		authenticationFilter.init(config);

		return authenticationFilter;
	}
}
