package com.kibou.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="testServlet",urlPatterns="/test")
public class TestServlet extends HttpServlet{
	
	private static final long serialVersionUID = -7846579429671711300L;

	public TestServlet(){
		System.out.println("TestServlet.TestServlet()");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("TestServlet.doGet()");
		resp.getWriter().println("Hello Test");
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
