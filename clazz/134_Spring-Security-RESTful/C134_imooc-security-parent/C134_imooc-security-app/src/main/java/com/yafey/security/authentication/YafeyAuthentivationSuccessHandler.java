package com.yafey.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yafey.security.core.properties.LoginResponseType;
import com.yafey.security.core.properties.SecurityProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("yafeyAuthentivationSuccessHandler")
// SavedRequestAwareAuthenticationSuccessHandler 类是 Spring Security 提供的默认的登录成功处理器
public class YafeyAuthentivationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private SecurityProperties securityProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.info("登录成功");
		if (LoginResponseType.JSON.equals(securityProperties.getBrowser().getLoginType())) {
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(authentication));
		} else {
			// 父类处理方式 就是 redirect 。
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}
}