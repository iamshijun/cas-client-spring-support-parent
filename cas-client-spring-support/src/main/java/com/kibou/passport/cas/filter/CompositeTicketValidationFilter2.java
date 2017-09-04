package com.kibou.passport.cas.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.jasig.cas.client.validation.AbstractTicketValidationFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.DelegatingTicketValidationFilter;

import com.kibou.passport.cas.filter.support.AbstractServerBasedCompositeCasFilter;
import com.kibou.passport.cas.handler.TicketValidationHandler;

/**
 * @author aimysaber@gmail.com
 */
@Deprecated
public class CompositeTicketValidationFilter2 extends AbstractServerBasedCompositeCasFilter<AbstractTicketValidationFilter> { 
	
	private TicketValidationHandler ticketValidationHandler;
	
	//@NotNull
	private Class<? extends AbstractTicketValidationFilter> ticketValidationFilterType =
			Cas30ProxyReceivingTicketValidationFilter.class;

	public void setTicketValidationFilterType(
			Class<? extends AbstractTicketValidationFilter> ticketValidationFilterType) {
		this.ticketValidationFilterType = ticketValidationFilterType;
	}
	
	
	public void setTicketValidationHandler(TicketValidationHandler ticketValidationHandler) {
		this.ticketValidationHandler = ticketValidationHandler;
	}
	
	@Override
	protected AbstractTicketValidationFilter createInnerFilter(String serverName, FilterConfig config, ServletRequest request)
			throws ServletException {
		
		DelegatingTicketValidationFilter ticketValidationFilter = 
				new DelegatingTicketValidationFilter(ticketValidationFilterType);
		
		ticketValidationFilter.setInnerServerName(serverName);
		ticketValidationFilter.setTicketValidationHandler(ticketValidationHandler);
		
		ticketValidationFilter.init(config);
		
		return ticketValidationFilter;
	}
	
	
}

 