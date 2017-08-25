<%@page contentType="text/html; charset=UTF-8" %>
<%@include file="global.jsp" %>
<html>
	<head>
		<title>index.jsp</title>
	</head>
	<body>
		<a href="#" onclick="sayHello()">sayHello</a><p>
		<a href="<%= basePath%>logout" >logout</a>
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
		<script>
			//(test AjaxAwareAuthenticationRedirectStrategy)
			//ajax request,remove browser cookie(sessionid) and check the response
			function sayHello(){
				$.ajax({
					url:"<%= basePath%>ajaxRequest",
					success:function(data){
						alert(data);
					}
				});	
			}	
		
		</script>
	</body>
</html>