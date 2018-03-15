##CAS-Client组件


#1. 配置 

	1-1. 使用者需要给定一个名称为: casPassportProperties 类型为Properties的spring bean
	  e.g : <util:properties id="casPassportProperties" location="classpath:cas-passport.properties"/> 

	1-2. 配置约定
	当前默认只会对以 "passport." 开头的配置进行读取. 拼接上具体filter (FilterWrapperBean,cas-passport-filter.xml中配置的Filter Bean)的名称
	   e.g authenticationFilter 会读取 以 "passport.authenticationFilter" 开头的配置项
	(ps : 暂时不支持前缀设置)
	
	1-9. 其他
	passport.security.ignorePattern 配置这个认证过滤链全局的需要忽略的链接(SecurityFilterChain) ,
	默认使用的路径匹配为 ServletPathMatcher(该类来源:alibaba/druid项目同名类) , 
	可通过passport.security.ignorePatternMatcherType 指定自定义的PathMatcher实例
	
	1-10. org.jasig.cas.client.session.SingleSignOutHttpSessionListener 配置放置在web-fragment.xml,
	如果使用servlet3 可以直接被引用(前提web.xml配置  metadata-complete="false")
	
#2. 组件

    2-1.  KeyedCompsiteFilter

	2-2.  FilterWrapperBean 
	类似spring的ServletWrappingController, 实际的行为有内部的Filter来执行, FilterConfig的配置由外部指定的Properties进行适配 .
	
