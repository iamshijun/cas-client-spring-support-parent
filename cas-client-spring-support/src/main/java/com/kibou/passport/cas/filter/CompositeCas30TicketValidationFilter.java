package com.kibou.passport.cas.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.AbstractTicketValidationFilter;
import org.jasig.cas.client.validation.Assertion;

import com.kibou.passport.cas.filter.support.AbstractCompositeCasFilter;
import com.kibou.passport.cas.filter.support.Cas30TicketValidationFilterSupport;
import com.kibou.passport.cas.handler.TicketValidationHandler;
import com.kibou.passport.util.WebUtils;

public class CompositeCas30TicketValidationFilter extends AbstractCompositeCasFilter<AbstractTicketValidationFilter> /*extends AbstractConfigurationFilter */{

	public CompositeCas30TicketValidationFilter(){
	}
	
	private TicketValidationHandler ticketValidationHandler;
	
	public void setTicketValidationHandler(TicketValidationHandler ticketValidationHandler) {
		this.ticketValidationHandler = ticketValidationHandler;
	}

	@Override
	public void doInternalFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		final HttpSession session = request.getSession(false);
		final Assertion assertion = session != null ? (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : null;
		
		if(assertion != null){
			filterChain.doFilter(request, response);
			return;
		}
			
		this.getInnerFilter(request, WebUtils.getBasePath(request))
				.doFilter(request, response, filterChain);
	}

	@Override
	protected AbstractTicketValidationFilter createInnerFilter(FilterConfig config, ServletRequest request)
			throws ServletException {
		
		String serverName = WebUtils.getBasePath((HttpServletRequest)request);
		
		Cas30TicketValidationFilterSupport ticketValidationFilter = 
				new Cas30TicketValidationFilterSupport(serverName,config);
		
		ticketValidationFilter.setTicketValidationHandler(ticketValidationHandler);
		
		ticketValidationFilter.init(config);
		
		return ticketValidationFilter;
	}
	
	
}

 