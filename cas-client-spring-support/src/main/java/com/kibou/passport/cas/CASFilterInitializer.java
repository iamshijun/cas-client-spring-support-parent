package com.kibou.passport.cas;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author aimysaber@gmail.com
 *
 */
public interface CASFilterInitializer {

	void onStartup(ServletContext servletContext) throws ServletException;
	
}
