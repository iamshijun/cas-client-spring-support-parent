package com.github.iamshijun.passport.cas.example.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.iamshijun.passport.cas.example.util.BeanFactoryUtil;
import com.github.iamshijun.passport.util.WebUtils;

/**
 * @author aimysaber@gmail.com
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet{ //移到组件中!
	
	private static final long serialVersionUID = 6630240824768164473L;
	
	private String casLogoutUrl;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		String casLogoutUrlParam = servletConfig.getInitParameter("casLogoutUrl");
		if(casLogoutUrlParam != null) {
			casLogoutUrl = casLogoutUrlParam;
		}else {
			Properties properties = BeanFactoryUtil.getBean("casPassportProperties", Properties.class);
			if(properties != null) {
				casLogoutUrl = properties.getProperty("passport.casLogoutUrl");
			}
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String serviceUrl = WebUtils.getBasePath(request);
		String from = request.getParameter("from");
		if(from != null && from.length() > 0){
			serviceUrl = from;
		}
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}

		String redirectUrl = casLogoutUrl + "?service=" + URLEncoder.encode(serviceUrl,"utf-8") ;
		response.sendRedirect(redirectUrl);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
}
