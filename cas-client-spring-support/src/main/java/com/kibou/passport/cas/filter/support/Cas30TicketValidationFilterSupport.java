package com.kibou.passport.cas.filter.support;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;

import com.kibou.passport.cas.handler.TicketValidationHandler;

public class Cas30TicketValidationFilterSupport extends Cas30ProxyReceivingTicketValidationFilter {

	private TicketValidationHandler ticketValidationHandler;
	
	private String serverName;
	
	public void setTicketValidationHandler(TicketValidationHandler ticketValidationHandler) {
		this.ticketValidationHandler = ticketValidationHandler;
	}
	
	public Cas30TicketValidationFilterSupport(TicketValidationHandler ticketValidationHandler) {
		this.ticketValidationHandler = ticketValidationHandler;
	}

	public Cas30TicketValidationFilterSupport(String serverName,FilterConfig filterConfig) throws ServletException {
		this.serverName = serverName;
	}
	
	@Override
	protected void initInternal(FilterConfig filterConfig) throws ServletException {
		super.initInternal(filterConfig);
		setServerName(serverName);
	}

	@Override
	protected void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response,
			Assertion assertion) {
		
		// -> 加到ticketValidationHandler(让他同时作为Filter => SRP)还是?这里 XXX
		//response.setHeader(name, value);
		Cookie cookie = new Cookie("MUSS", assertion.getPrincipal().getName());
		cookie.setHttpOnly(true);
		//cookie.setDomain(domain);
		cookie.setPath(request.getContextPath());
		//cookie.setSecure(true);
		response.addCookie(cookie);
		
		if (ticketValidationHandler != null) {
			ticketValidationHandler.onSuccessfulValidation(request, response, assertion);
		}
		super.onSuccessfulValidation(request, response, assertion);
	}

	@Override
	protected void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
		if (ticketValidationHandler != null) {
			ticketValidationHandler.onFailedValidation(request, response);
		}
		super.onFailedValidation(request, response);
	}
}