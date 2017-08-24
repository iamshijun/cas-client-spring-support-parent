package com.kibou.passport.cas.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas10TicketValidationFilter;

import com.kibou.passport.cas.filter.support.AbstractCompositeCasFilter;
import com.kibou.passport.cas.handler.TicketValidationHandler;
import com.kibou.passport.util.WebUtils;

import com.kibou.passport.cas.filter.CompositeTicketValidationFilter.TicketValidationFilterSupport;

public class CompositeTicketValidationFilter extends AbstractCompositeCasFilter<TicketValidationFilterSupport> /*extends AbstractConfigurationFilter */{

	public CompositeTicketValidationFilter(){
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
	protected TicketValidationFilterSupport createInnerFilter(FilterConfig config, ServletRequest request)
			throws ServletException {
		
		TicketValidationFilterSupport ticketValidationFilter = 
				new TicketValidationFilterSupport(WebUtils.getBasePath((HttpServletRequest)request), config);
		
		ticketValidationFilter.setTicketValidationHandler(ticketValidationHandler);
		
		return ticketValidationFilter;
	}
	
	class TicketValidationFilterSupport extends Cas10TicketValidationFilter{
		
		final String serverName;
		
		TicketValidationHandler ticketValidationHandler;
		
		public TicketValidationFilterSupport(String serverName,FilterConfig filterConfig) throws ServletException{
			this.serverName = Objects.requireNonNull(serverName);
			super.init(filterConfig);
		}
		
		public void setTicketValidationHandler(TicketValidationHandler ticketValidationHandler) {
			this.ticketValidationHandler = ticketValidationHandler;
		}
		
		@Override
		public void init() {
			super.setServerName(serverName);
			super.init();
		}
		
		@Override
		protected void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response,
				Assertion assertion) {
			if(ticketValidationHandler != null){
				ticketValidationHandler.onSuccessfulValidation(request, response, assertion);
			}
			super.onSuccessfulValidation(request, response, assertion);
		}
		
		@Override
		protected void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
			if(ticketValidationHandler != null){
				ticketValidationHandler.onFailedValidation(request, response);
			}
			super.onFailedValidation(request, response);
		}
	}
}

 