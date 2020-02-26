package com.yafey.security.core.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.yafey.security.core.properties.SecurityConstants;

public class AbstractChannelSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	protected AuthenticationSuccessHandler imoocAuthenticationSuccessHandler;
	
	@Autowired
	protected AuthenticationFailureHandler imoocAuthenticationFailureHandler;
	
	protected void applyPasswordAuthenticationConfig(HttpSecurity http) throws Exception {
		http.formLogin()  // 认证方式
			.loginPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)  // 当请求需要身份认证时，默认跳转的url
			.loginProcessingUrl(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM)  // 自定义表单 处理请求
			.successHandler(imoocAuthenticationSuccessHandler)   // 配置成功处理器
			.failureHandler(imoocAuthenticationFailureHandler);  // 配置失败处理器
	}
	
}
