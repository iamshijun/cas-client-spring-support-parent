package org.jasig.cas.client.authentication;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.jasig.cas.client.configuration.ConfigurationKeys;

@Deprecated
public class DelegatingAuthenticationFilter extends AuthenticationFilter{
	
	private String innerServerName;
	
	public void setInnerServerName(String innerServerName) {
		this.innerServerName = innerServerName;
	}
	
	/* 注意到父类 AbstractCasFilter的 init(FilterConfig)-> init() .
	 * AuthenticationFilter的init()开始对serverName / service 进行空值判断
	 * 这里在初始化的时候执行setServerName的话 , 最终被这里的init方法中的
	 * setServerName(getString(ConfigurationKeys.SERVER_NAME));设置为空(如果filterConfig没有配置的话)
	 * 所以当前方法添加了setInnerServerName保存serverName设置 并重写initInternal / init 设置父类的serverName属性
	 * 
	    super.init(filterConfig);
        if (!isIgnoreInitConfiguration()) {
            setServerName(getString(ConfigurationKeys.SERVER_NAME));
            setService(getString(ConfigurationKeys.SERVICE));
            setEncodeServiceUrl(getBoolean(ConfigurationKeys.ENCODE_SERVICE_URL));
            
            initInternal(filterConfig);
        }
        init(); 
	 * 
	 */
	
	@Override
	protected void initInternal(FilterConfig filterConfig) throws ServletException {
		//如果配置中没有设置serverName 使用当前对象中设置的innerServerName
		String serverName = getString(ConfigurationKeys.SERVER_NAME);
		if(serverName == null || serverName.length() == 0) {
			setServerName(innerServerName);
		}
		
		super.initInternal(filterConfig);
	}
	
}
