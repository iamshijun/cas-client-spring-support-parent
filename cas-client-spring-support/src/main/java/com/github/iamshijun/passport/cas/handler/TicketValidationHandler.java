package com.github.iamshijun.passport.cas.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.validation.Assertion;

/**
 * @author aimysaber@gmail.com
 *
 */
public interface TicketValidationHandler {

	void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response, Assertion assertion);

	void onFailedValidation(HttpServletRequest request, HttpServletResponse response);
}
