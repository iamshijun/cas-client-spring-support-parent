package com.github.iamshijun.passport.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;

/**
 * @author aimysaber@gmail.com
 *
 */
public abstract class CasClientUtils {
	private CasClientUtils() {}
	
	public static Assertion getAssertion(HttpServletRequest request) {
		
		//AssertionHolder.getAssertion();
		
		final HttpSession session = request.getSession(false);
		//return session != null ? (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : null;

		final Assertion assertion = (Assertion) (session == null ? request
	                .getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : session
	                .getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION));
		
		return assertion;
	}
	
}
