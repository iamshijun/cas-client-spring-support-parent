package com.github.iamshijun.passport.cas.filter.support;

import org.jasig.cas.client.authentication.AuthenticationFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author aimysaber@gmail.com
 */
public class AuthenticationFilterSupport extends AuthenticationFilter {

    private String serverName;

    public AuthenticationFilterSupport(String serverName, FilterConfig filterConfig) throws ServletException {
        this.serverName = serverName;
    }

    @Override
    protected void initInternal(FilterConfig filterConfig) throws ServletException {
        super.initInternal(filterConfig);
        setServerName(serverName);
    }

    @Override
    public String retrieveTicketFromRequest(HttpServletRequest request) {
        return super.retrieveTicketFromRequest(request);
    }

    public boolean isTicketRequest(HttpServletRequest request) {
        String ticket = retrieveTicketFromRequest(request);
        return ticket != null && !ticket.isEmpty();
    }
}