package org.jasig.cas.client.validation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.util.CasClients;

import com.kibou.passport.cas.handler.TicketValidationHandler;

@Deprecated
public class DelegatingTicketValidationFilter extends AbstractTicketValidationFilter{
	
	private final Protocol protocol;
	
	private final AbstractTicketValidationFilter delegate;
	
	private static Map<Protocol,Class<? extends AbstractTicketValidationFilter>> protocolTicketValidationFilterTypeMap = new HashMap<>();
	private static Map<Class<? extends AbstractTicketValidationFilter>,Protocol> ticketValidationFilterTypeProtocolMap = new HashMap<>();
	
	static {
		protocolTicketValidationFilterTypeMap.put(Protocol.CAS1, Cas10TicketValidationFilter.class);
		protocolTicketValidationFilterTypeMap.put(Protocol.CAS2, Cas20ProxyReceivingTicketValidationFilter.class);
		protocolTicketValidationFilterTypeMap.put(Protocol.CAS3, Cas30ProxyReceivingTicketValidationFilter.class);
		
		ticketValidationFilterTypeProtocolMap.put(Cas10TicketValidationFilter.class, Protocol.CAS1);
		ticketValidationFilterTypeProtocolMap.put(Cas20ProxyReceivingTicketValidationFilter.class, Protocol.CAS2);
		ticketValidationFilterTypeProtocolMap.put(Cas30ProxyReceivingTicketValidationFilter.class, Protocol.CAS3);
	}
	//
	private TicketValidationHandler ticketValidationHandler;
	
	public void setTicketValidationHandler(TicketValidationHandler ticketValidationHandler) {
		this.ticketValidationHandler = ticketValidationHandler;
	}

	public DelegatingTicketValidationFilter() {
		this(Protocol.CAS3);
	}
	
	public DelegatingTicketValidationFilter(Protocol protocol) {
		this(protocol,protocolTicketValidationFilterTypeMap.get(protocol));
	}
	
	public DelegatingTicketValidationFilter(Class<? extends AbstractTicketValidationFilter> type) {
		this(Objects.requireNonNull(ticketValidationFilterTypeProtocolMap.get(type)),type);
	}
	
	public DelegatingTicketValidationFilter(AbstractTicketValidationFilter delegate) {
		super(CasClients.getCASFilterProtocol(delegate));
		
		this.protocol = CasClients.getCASFilterProtocol(delegate);
		this.delegate = Objects.requireNonNull(delegate);
	}
	
	public DelegatingTicketValidationFilter(Protocol protocol,Class<? extends AbstractTicketValidationFilter> ticketValidationFilterType) {
		super(protocol);
		
		this.protocol = protocol;
		
		try { //try get Constructor which contain Protocol parameter 
			this.delegate = ticketValidationFilterType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private String innerServerName;
	
	public void setInnerServerName(String innerServerName) {
		this.innerServerName = innerServerName;
	}
	
	@Override
	protected void initInternal(FilterConfig filterConfig) throws ServletException {

		String serverName = getString(ConfigurationKeys.SERVER_NAME);
		if(serverName == null || serverName.length() == 0) {
			setServerName(innerServerName);
		}
		delegate.init(filterConfig);
		delegate.initInternal(filterConfig);
		
		super.initInternal(filterConfig);
	}
	
	@Override
	public HostnameVerifier getHostnameVerifier() {
		return delegate.getHostnameVerifier();
	}
	
	@Override
	public Protocol getProtocol() {
		return protocol;
	}
	
	@Override
	public Properties getSSLConfig() {
		return delegate.getSSLConfig();
	}
	
	@Override
	public TicketValidator getTicketValidator(FilterConfig filterConfig) {
		return delegate.getTicketValidator(filterConfig);
	}
	
	@Override
	public boolean preFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		return delegate.preFilter(servletRequest, servletResponse, filterChain);
	}
	
	@Override
	public String retrieveTicketFromRequest(HttpServletRequest request) {
		return CasClients.retrieveTicketFromRequest(delegate, request);
	}
	
	@Override
	public void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response,
			Assertion assertion) {
		if(ticketValidationHandler != null) {
			ticketValidationHandler.onSuccessfulValidation(request, response, assertion);
		}
		super.onSuccessfulValidation(request, response, assertion);
	}
	
	
	@Override
	public void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
		if(ticketValidationHandler != null) {
			ticketValidationHandler.onFailedValidation(request, response);
		}
		super.onFailedValidation(request, response);
	}
}
