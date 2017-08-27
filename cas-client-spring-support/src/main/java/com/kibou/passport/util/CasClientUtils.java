package com.kibou.passport.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;

public abstract class CasClientUtils {
	private CasClientUtils() {}
	
	public static Assertion getAssertion(HttpServletRequest request) {
		final HttpSession session = request.getSession(false);
		return session != null ? (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : null;
	}
	
}
