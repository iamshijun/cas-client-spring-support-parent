package com.kibou.passport.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author aimysaber@gmail.com
 *
 */
public class WebUtils {
	
	public static String getBasePath(HttpServletRequest request){
		return request.getScheme() + "://" +
				request.getServerName() + 
				(request.getServerPort() == 80 ? "" : ":"+ request.getServerPort()) +
				request.getContextPath();
	}
	
	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if(ip != null && ip.indexOf(",") != -1){//x-forwared-for 经过了多个load-balancer/agent
			String[] forwardedIps = ip.split(",");
			ip = forwardedIps[0];
		}
		return ip;
	}
	
	public static boolean isAjaxRequest(HttpServletRequest request){  
		//Accept:Accept:text/javascript, application/javascript, application/ecmascript, application/x-ecmascript...
	    return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));  
	}  
	
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null && cookies.length > 0) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals(cookieName)) {
					return cookie;
				}
			}
		}
		return null;
	}
}
