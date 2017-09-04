package org.jasig.cas.client.util;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.Protocol;

/**
 * @author aimysaber@gmail.com
 *
 */
@Deprecated
public abstract class CasClients {

	public static Protocol getCASFilterProtocol(AbstractCasFilter casFilter) {
		return casFilter.getProtocol();
	}
	
	public static String retrieveTicketFromRequest(AbstractCasFilter casFilter, HttpServletRequest request) {
		return casFilter.retrieveTicketFromRequest(request);
	}
		
}
