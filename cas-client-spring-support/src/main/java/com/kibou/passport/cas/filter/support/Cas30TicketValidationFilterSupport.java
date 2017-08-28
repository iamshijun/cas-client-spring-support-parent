package com.kibou.passport.cas.filter.support;

import java.util.List;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;

import com.kibou.passport.cas.handler.TicketValidationHandler;
import com.kibou.passport.util.CollectionUtils;

public class Cas30TicketValidationFilterSupport extends Cas30ProxyReceivingTicketValidationFilter {

	private List<TicketValidationHandler> ticketValidationHandlers;
	
	private String serverName;
	
	public List<TicketValidationHandler> getTicketValidationHandlers() {
		return ticketValidationHandlers;
	}

	public void setTicketValidationHandlers(List<TicketValidationHandler> ticketValidationHandlers) {
		this.ticketValidationHandlers = ticketValidationHandlers;
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
		
		if (CollectionUtils.isNotEmpty(ticketValidationHandlers)) {
			for(TicketValidationHandler handler : ticketValidationHandlers) {
				handler.onSuccessfulValidation(request, response, assertion);
			}
		}
		super.onSuccessfulValidation(request, response, assertion);
	}

	@Override
	protected void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
		if (CollectionUtils.isNotEmpty(ticketValidationHandlers)) {
			for(TicketValidationHandler handler : ticketValidationHandlers) {
				handler.onFailedValidation(request, response);
			}
		}
		super.onFailedValidation(request, response);
	}
}