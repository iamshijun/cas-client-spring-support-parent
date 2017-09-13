package com.github.iamshijun.passport.cas.example.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BeanFactoryUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;


	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	public static <T> Map<String, T> getBeanOfType(Class<T> type) {
		return applicationContext.getBeansOfType(type);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		BeanFactoryUtil.applicationContext = applicationContext;
	}

}
