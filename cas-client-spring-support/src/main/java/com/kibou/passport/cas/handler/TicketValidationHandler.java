package com.kibou.passport.cas.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.validation.Assertion;

public interface TicketValidationHandler {

	void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response, Assertion assertion);

	void onFailedValidation(HttpServletRequest request, HttpServletResponse response);
}
