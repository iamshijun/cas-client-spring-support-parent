package com.kibou.passport.cas.filter.support;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
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