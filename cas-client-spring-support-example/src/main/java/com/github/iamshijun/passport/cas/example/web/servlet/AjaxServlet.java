package com.github.iamshijun.passport.cas.example.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;

/**
 * @author aimysaber@gmail.com
 *
 */
@WebServlet(name = "ajaxServlet" , urlPatterns = "/ajaxRequest")
public class AjaxServlet extends HttpServlet{

	private static final long serialVersionUID = -6398547149123337905L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Assertion assertion = AssertionHolder.getAssertion();
		String name = "World!";
		if(assertion != null) {
			name = assertion.getPrincipal().getName();
		}
		resp.getWriter().println("Hello " + name);
		resp.flushBuffer();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}
}
