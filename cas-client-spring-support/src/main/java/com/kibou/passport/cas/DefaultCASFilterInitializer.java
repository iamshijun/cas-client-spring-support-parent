package com.kibou.passport.cas;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;

import com.kibou.passport.util.Assert;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

//2.--实现方式2 使用HandlerType + Tomcat的SPI(META-INF/services/javax.servlet.ServletContainerInitializer)提供
/**
 * @author aimysaber@gmail.com
 *
 */
public class DefaultCASFilterInitializer implements CASFilterInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		System.out.println("DefaultCASFilterInitializer.onStartup()");
		
		FilterRegistration characterEncodingFilterRegistration = servletContext.getFilterRegistration("characterEncodingFilter");
		servletContext.log("characterEncodingFilterRegistration :" + characterEncodingFilterRegistration);
		
//		servletContext.getFilterRegistrations()
		
		
		/*Filter[] filters = getServletFilters();
		if (!ObjectUtils.isEmpty(filters)) {
			for (Filter filter : filters) {
				registerServletFilter(servletContext, filter);
			}
		}*/

	}

	protected FilterRegistration.Dynamic registerServletFilter(
			ServletContext servletContext, Filter filter, String filterName,String[] urlPatterns) {
		
		Dynamic registration = servletContext.addFilter(filterName, filter);
		
		if (registration == null) {
			int counter = -1;
			while (counter == -1 || registration == null) {
				counter++;
				registration = servletContext.addFilter(filterName + "#" + counter, filter);
				Assert.isTrue(counter < 100, "Failed to register filter '" + filter + "'."
						+ "Could the same Filter instance have been registered already?");
			}
		}
		registration.setAsyncSupported(isAsyncSupported());
		//EnumSet<DispatcherType> : EnumSet.of(DispatcherType.FORWARD)...,
		registration.addMappingForUrlPatterns(null, false, urlPatterns);
		
		//registration.addMappingForServletNames(getDispatcherTypes(), false, getServletName());
		return registration;
	}


	private boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}
}
