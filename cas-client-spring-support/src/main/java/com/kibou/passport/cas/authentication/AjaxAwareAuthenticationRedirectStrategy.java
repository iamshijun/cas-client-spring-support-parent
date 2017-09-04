package com.kibou.passport.cas.authentication;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;

import com.kibou.passport.util.WebUtils;

/**
 * @author aimysaber@gmail.com
 *
 */
public class AjaxAwareAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {

	private String ajaxResponse = "needLogin";
	
	public void setAjaxResponse(String ajaxResponse) {
		this.ajaxResponse = ajaxResponse;
	}
	
	@Override
	public void redirect(HttpServletRequest request, HttpServletResponse response, String potentialRedirectUrl)
			throws IOException {

		if (WebUtils.isAjaxRequest(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().println(ajaxResponse);//自定义返回?
			response.flushBuffer();
		} else {
			response.sendRedirect(potentialRedirectUrl);
		}
	}

}
