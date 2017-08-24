package com.kibou.passport.cas;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public interface CASFilterInitializer {

	void onStartup(ServletContext servletContext) throws ServletException;
	
}
