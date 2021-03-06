package com.github.iamshijun.passport.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

//FIXME(shisj) 权衡下 将listener放在这里还是放在 web-fragement.xml
/*
 * <listener>
		<listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
	</listener> 
 *
 */
//@WebListener 
/**
 * @author aimysaber@gmail.com
 *
 */
public class SingleSignOutHttpSessionListenerDelegator implements HttpSessionListener{

	//TODO(shisj) change with HttpSessionListener not a specfied type
	private final org.jasig.cas.client.session.SingleSignOutHttpSessionListener delegator;
	
	
	public SingleSignOutHttpSessionListenerDelegator(){
		delegator =  new org.jasig.cas.client.session.SingleSignOutHttpSessionListener();
	}
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		delegator.sessionCreated(se);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		delegator.sessionDestroyed(se);
	}

}
