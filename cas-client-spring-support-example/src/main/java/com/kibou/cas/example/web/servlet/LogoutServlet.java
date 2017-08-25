package com.kibou.cas.example.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kibou.passport.util.WebUtils;

@WebServlet(name = "logoutServlet" , urlPatterns ="/logout")
public class LogoutServlet extends HttpServlet{
	
	private String casLogoutUrl = "https://cas.example.com/cas/logout";
	
	private static final long serialVersionUID = 6630240824768164473L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String serviceUrl = WebUtils.getBasePath(request);
		String from = request.getParameter("from");
		if(from != null && from.length() > 0){
			serviceUrl = from;
		}

		String redirectUrl = casLogoutUrl + "?service=" + URLEncoder.encode(serviceUrl,"utf-8") ;
		response.sendRedirect(redirectUrl);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
}
