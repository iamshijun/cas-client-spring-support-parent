package com.github.iamshijun.passport.cas.filter;

import com.github.iamshijun.passport.cas.filter.support.AbstractServerBasedCompositeCasFilter;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.authentication.DelegatingAuthenticationFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author aimysaber@gmail.com
 */
@Deprecated
public class CompositeAuthenticationFilter2 extends AbstractServerBasedCompositeCasFilter<AuthenticationFilter> {

    @Override
    protected AuthenticationFilter createInnerFilter(String serverName, FilterConfig config, HttpServletRequest request) throws ServletException {

        DelegatingAuthenticationFilter authenticationFilter = new DelegatingAuthenticationFilter();
        authenticationFilter.setInnerServerName(serverName);

        authenticationFilter.init(config);

        return authenticationFilter;
    }
}
 