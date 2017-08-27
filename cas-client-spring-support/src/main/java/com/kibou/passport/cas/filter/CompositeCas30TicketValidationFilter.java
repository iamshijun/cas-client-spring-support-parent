package com.kibou.passport.cas.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.jasig.cas.client.validation.AbstractTicketValidationFilter;

import com.kibou.passport.cas.filter.support.AbstractServerBasedCompositeCasFilter;
import com.kibou.passport.cas.filter.support.Cas30TicketValidationFilterSupport;
import com.kibou.passport.cas.handler.TicketValidationHandler;

public class CompositeCas30TicketValidationFilter 
				extends AbstractServerBasedCompositeCasFilter<AbstractTicketValidationFilter>{ 

	private TicketValidationHandler ticketValidationHandler;
	
	public void setTicketValidationHandler(TicketValidationHandler ticketValidationHandler) {
		this.ticketValidationHandler = ticketValidationHandler;
	}

	@Override
	protected AbstractTicketValidationFilter createInnerFilter(String serverName, FilterConfig config, ServletRequest request)
			throws ServletException {
		
		Cas30TicketValidationFilterSupport ticketValidationFilter = 
				new Cas30TicketValidationFilterSupport(serverName,config);
		
		ticketValidationFilter.setTicketValidationHandler(ticketValidationHandler);
		
		ticketValidationFilter.init(config);
		
		return ticketValidationFilter;
	}
	
	
}

 