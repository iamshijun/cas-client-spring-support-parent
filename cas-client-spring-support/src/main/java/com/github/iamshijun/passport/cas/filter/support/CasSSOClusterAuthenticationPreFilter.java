package com.github.iamshijun.passport.cas.filter.support;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.github.iamshijun.passport.cas.cache.AssertionStorage;
import com.github.iamshijun.passport.cas.handler.TicketValidationHandler;
import com.github.iamshijun.passport.util.CasClientUtils;
import com.github.iamshijun.passport.util.WebUtils;


/**
 * 在当前架构没有实现session同步的情况下使用该类来实现 一个service后面多个服务器之间的模拟登录(一台登录过 如果Load balancer转发到其他机器时会通过此filter检验出登录状态)
 * 配合自定义的TicketValidationFilter(成功登录后会在cookie存储一个标识,
 * 分布式缓存一个key/value=key同前面的cookie存储哦标识,value为Assertion的信息 用于在其他机器再将Assertion重新构造出来)<br>
 * 
 * 如需启用当前filter,需要指定AssertionStorage(name=assertionStorage)的具体实现
 * 
 * @author aimysaber@gmail.com
 *
 */
public class CasSSOClusterAuthenticationPreFilter implements Filter, TicketValidationHandler{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CasSSOClusterAuthenticationPreFilter.class);
	
	private String cookieName = "KUSS";
	
	private boolean switchOn = true;//on
	
	private AssertionStorage assertionStorage;//
	
	private String logoutUrl = "/logout"; 
	
	private boolean cookieSecurity = false;
	
	private String cookiePath = "/" ;
	
	//private CipherExecutor(see : cas-server)

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@Autowired (required=false)
	@Qualifier("assertionStorage")
	public void setAssertionStorage(AssertionStorage assertionStorage) {
		this.assertionStorage = assertionStorage;
	}

	@Autowired (required=false)
	public void setCookiePath(String cookiePath){
		this.cookiePath = cookiePath;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException {
		
		if(!isPreFilterEnabled()) {
			filterChain.doFilter(req, resp);
			return;
		}
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		if(request.getRequestURL().toString().contains(logoutUrl)) {
			LOGGER.debug("Logout request, remove cookie");
			removeCookie(request,response);
			filterChain.doFilter(req, resp);
			return;
		}
		
		//如果当前是ticket返回 不处理
		String ticket = CommonUtils.safeGetParameter(request, 
				Protocol.CAS3.getArtifactParameterName());//暂时硬编码Protocol - 
		if (CommonUtils.isNotBlank(ticket)) {
			//removeCookie(request,response);
			filterChain.doFilter(request, response);
            return;
		}
		
		final Assertion assertion = CasClientUtils.getAssertion(request);
		
		if (assertion == null) {
			
			LOGGER.debug("Request URL : " + request.getRequestURL());
			
			Cookie cookie = WebUtils.getCookie(request, cookieName);
			if (cookie != null) {
				
				String kuss = cookie.getValue(); //validate(TODO) ...
				LOGGER.debug("Found sso cookie " + kuss);
				
				Assertion storedAssertion = assertionStorage.get(kuss);
				
				if(storedAssertion != null) {
					LOGGER.debug("Found shared assertion object from storage");
					
					putAssertionIntoConversation(request, storedAssertion);
				}else{
					LOGGER.debug("Found none assertion object from storage, remove cookie");

					removeCookie(request,response);
					//what next
				}
			}
		}
		
		filterChain.doFilter(req, resp);
		return;
	}

	protected void putAssertionIntoConversation(HttpServletRequest request,Assertion assertion ){
		
		//create session immediately if it is not a ajax request?
		HttpSession session = request.getSession(WebUtils.isAjaxRequest(request) ? false : true);
		if(session != null) {
			session.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION, assertion); 
		}else {
			request.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION, assertion);
		}

	}

	@Override
	public void destroy() {
		
	}
	
	@Override
	public void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response, Assertion assertion) {
		LOGGER.debug("Ticket validation success");
		
		if(isPreFilterEnabled()) {
			//自定义的客户端用的 ticket/token/cipher 密串,这里没有安全问题?
			// 暂时单纯的使用一个UUID作为key TODO 增加过期等信息到密文中
			@SuppressWarnings("restriction")
			String kuss = new sun.misc.BASE64Encoder().encode(UUID.randomUUID().toString().getBytes());
			//String cipher = Base64Utils.encodeToString(UUID.randomUUID().toString().getBytes());
			
			assertionStorage.put(kuss, assertion);//上面的createAssertion需要配合 知道这里存储的对象类型! 
			
			addCookie(request, response, kuss);
			
			LOGGER.debug("add sso cookie to path : " + request.getContextPath());
		}
		
	}

	@Override
	public void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
		LOGGER.debug("Ticket validation failed");
		removeCookie(request,response);
	}
	
	private void addCookie(HttpServletRequest request, HttpServletResponse response, String enabledAssertion) {
		Cookie cookie = new Cookie(cookieName, enabledAssertion);

		//TODO : check if cookiePath startsWith "/"
		String path = cookiePath == null ? "/" : cookiePath;
		cookie.setHttpOnly(true);
		//cookie.setDomain(request.getHost());
		cookie.setPath(path);
		if(cookieSecurity) {
			cookie.setSecure(true);
		}
		
		response.addCookie(cookie);
	}
	private void removeCookie(HttpServletRequest request, HttpServletResponse response){
		Cookie cookie = new Cookie(cookieName, "");

		String path = cookiePath == null ? "/" : cookiePath;
		//fix 这里如果设置成contextPath的话 对分别在 foo.com 和 foo.com/bar 的两个独立应用来说会存在问题 
		cookie.setPath(path);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
	
	public void setCookieSecurity(boolean cookieSecurity) {
		this.cookieSecurity = cookieSecurity;
	}
	
	//set logoutUrl pattern
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}
	
	public void setSwitchOn(boolean switchOn) {
		this.switchOn = switchOn;
	}
	
	private boolean isPreFilterEnabled() {
		if(switchOn) {
			return assertionStorage != null;
		}
		return false;
	}

}
