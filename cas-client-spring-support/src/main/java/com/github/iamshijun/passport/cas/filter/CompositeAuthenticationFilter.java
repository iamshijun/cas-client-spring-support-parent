package com.github.iamshijun.passport.cas.filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AuthenticationFilter;

import com.github.iamshijun.passport.cas.filter.support.AbstractServerBasedCompositeCasFilter;
import com.github.iamshijun.passport.cas.filter.support.AuthenticationFilterSupport;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;

import java.io.IOException;

/**
 * @author aimysaber@gmail.com
 */
public class CompositeAuthenticationFilter extends AbstractServerBasedCompositeCasFilter<AuthenticationFilterSupport>
		/* extends org.jasig.cas.client.util.AbstractConfigurationFilter */ {

	@Override
	protected AuthenticationFilterSupport createInnerFilter(String serverName,FilterConfig config, HttpServletRequest request)
			throws ServletException {

		AuthenticationFilterSupport authenticationFilter = new AuthenticationFilterSupport(serverName,config);
		authenticationFilter.init(config);

		return authenticationFilter;
	}

	@Override
	protected void doInternalFilter(AuthenticationFilterSupport innerFilter,
									HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		if (innerFilter.isTicketRequest(request) || //如果是携带Ticket的请求
				getAssertion(request, response) != null) {//或者是已经登录过(能够找到Assertion)

			filterChain.doFilter(request, response);

			return;
		}

		super.doInternalFilter(innerFilter,request, response, filterChain);
	}

	protected Assertion getAssertion(HttpServletRequest request, HttpServletResponse response) {
		final HttpSession session = request.getSession(false);

		return (Assertion)
				(session == null ?
						request.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) :
						session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION));
	}

}
