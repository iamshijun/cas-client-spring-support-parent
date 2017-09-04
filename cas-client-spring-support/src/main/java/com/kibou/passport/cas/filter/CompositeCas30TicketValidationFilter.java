package com.kibou.passport.cas.filter;

import java.util.List;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.jasig.cas.client.validation.AbstractTicketValidationFilter;

import com.kibou.passport.cas.filter.support.AbstractServerBasedCompositeCasFilter;
import com.kibou.passport.cas.filter.support.Cas30TicketValidationFilterSupport;
import com.kibou.passport.cas.handler.TicketValidationHandler;

/**
 * @author aimysaber@gmail.com
 *
 */
public class CompositeCas30TicketValidationFilter
		extends AbstractServerBasedCompositeCasFilter<AbstractTicketValidationFilter> {

	private List<TicketValidationHandler> ticketValidationHandlers;

	public void setTicketValidationHandlers(List<TicketValidationHandler> ticketValidationHandlers) {
		this.ticketValidationHandlers = ticketValidationHandlers;
		System.out.println("@@@@@@@@ ticketValidationHandlers size :" + 
				(ticketValidationHandlers == null ? 0 : ticketValidationHandlers.size()) + " @@@@@@@@");
	}
	
	@Override
	protected AbstractTicketValidationFilter createInnerFilter(String serverName, FilterConfig config,
			ServletRequest request) throws ServletException {

		Cas30TicketValidationFilterSupport ticketValidationFilter = 
				new Cas30TicketValidationFilterSupport(serverName, config);

		ticketValidationFilter.setTicketValidationHandlers(ticketValidationHandlers);

		ticketValidationFilter.init(config);

		return ticketValidationFilter;
	}

}
