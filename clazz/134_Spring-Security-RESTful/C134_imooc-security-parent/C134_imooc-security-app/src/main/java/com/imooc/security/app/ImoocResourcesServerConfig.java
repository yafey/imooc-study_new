package com.imooc.security.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SpringSocialConfigurer;

import com.yafey.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.yafey.security.core.properties.SecurityConstants;
import com.yafey.security.core.properties.SecurityProperties;
import com.yafey.security.core.social.SocialConfig;
import com.yafey.security.core.validate.code.ValidateCodeSecurityConfig;

@Configuration
@EnableResourceServer
public class ImoocResourcesServerConfig extends ResourceServerConfigurerAdapter {
	@Autowired
	protected AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	protected AuthenticationFailureHandler authenticationFailureHandler;

	@Autowired
	private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

	@Autowired
	private ValidateCodeSecurityConfig validateCodeSecurityConfig;

	/**
	 * @see SocialConfig#socialSecurityConfig()
	 */
	@Autowired
	private SpringSocialConfigurer socialSecurityConfig;

	@Autowired
	private SecurityProperties securityProperties;

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.formLogin().loginPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)
				.loginProcessingUrl(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM)
				.successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler);

		// 验证码有一些问题：因为验证码会放到session里，校验也需要从session里取，
		// 但是在token认证的情景下是不需session参与的
		// 所以这里先注释掉，下篇文章再解决
		http// .apply(validateCodeSecurityConfig)
			// .and()
				.apply(smsCodeAuthenticationSecurityConfig).and().apply(socialSecurityConfig).and().authorizeRequests()
				.antMatchers(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
						SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE,
						securityProperties.getBrowser().getLoginPage(),
						SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX + "/*",
						securityProperties.getBrowser().getSignUpUrl(),
						securityProperties.getBrowser().getSession().getSessionInvalidUrl(),
						securityProperties.getBrowser().getSignOutUrl(), "/user/regist")
				.permitAll().anyRequest().authenticated().and().csrf().disable();
	}
}