<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
	String path = request.getContextPath();  
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	
	request.setAttribute("path",path);
	request.setAttribute("basePath",basePath);
%>