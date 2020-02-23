package com.yafey.security.browser;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yafey.security.browser.support.SimpleResponse;
import com.yafey.security.core.properties.SecurityProperties;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BrowserSecurityController {

	private RequestCache requestCache = new HttpSessionRequestCache(); // 将请求缓存到 session 里面

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy(); // Spring redirect 的 工具类

	@Autowired
	private SecurityProperties securityProperties;

	/**
	 * 当需要身份认证时，跳转到这里
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/authentication/require")
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public SimpleResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		SavedRequest savedRequest = requestCache.getRequest(request, response);

		if (savedRequest != null) {
			String targetUrl = savedRequest.getRedirectUrl();
			log.info("引发跳转的请求是:" + targetUrl);
	        //判断引发跳转的是html 还是 不是html
			if (StringUtils.endsWithIgnoreCase(targetUrl, ".html")) {
				redirectStrategy.sendRedirect(request, response
						, securityProperties.getBrowser().getLoginPage()  // 可以使用自定义的 login 页面，或者使用默认的页面
						);
			}
		}
		return new SimpleResponse("访问的服务需要身份认证，请引导用户到登录页");
	}

}