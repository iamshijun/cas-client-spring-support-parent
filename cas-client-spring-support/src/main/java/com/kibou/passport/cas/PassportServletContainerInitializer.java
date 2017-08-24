package com.kibou.passport.cas;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

@HandlesTypes(CASFilterInitializer.class)
public class PassportServletContainerInitializer implements ServletContainerInitializer{

	@Override
	public void onStartup(Set<Class<?>> casFilterInitializerClasses, ServletContext servletContext) throws ServletException {
		
		List<CASFilterInitializer> initializers = new LinkedList<CASFilterInitializer>();

		if (casFilterInitializerClasses != null) {
			for (Class<?> cfiClass : casFilterInitializerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...  <== (ps:spring comment)
				if (!cfiClass.isInterface() && !Modifier.isAbstract(cfiClass.getModifiers()) &&
						CASFilterInitializer.class.isAssignableFrom(cfiClass)) {
					try {
						initializers.add((CASFilterInitializer) cfiClass.newInstance());
					}
					catch (Throwable ex) {
						throw new ServletException("Failed to instantiate PassportServletContainerInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			servletContext.log("No Passport ServletContainerInitializer types detected on classpath");
			return;
		}

		//AnnotationAwareOrderComparator.sort(initializers);
		servletContext.log("Passport ServletContainerInitializers detected on classpath: " + initializers);

		for (CASFilterInitializer initializer : initializers) {
			initializer.onStartup(servletContext);
		}
	}

}
