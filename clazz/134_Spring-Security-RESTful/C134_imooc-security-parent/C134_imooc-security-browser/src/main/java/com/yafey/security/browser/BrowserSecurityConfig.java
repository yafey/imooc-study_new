package com.yafey.security.browser;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.security.SpringSocialConfigurer;

import com.yafey.security.browser.session.YaFeyExpiredSessionStrategy;
import com.yafey.security.core.authentication.AbstractChannelSecurityConfig;
import com.yafey.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.yafey.security.core.properties.SecurityConstants;
import com.yafey.security.core.properties.SecurityProperties;
import com.yafey.security.core.validate.code.ValidateCodeSecurityConfig;

@Configuration
public class BrowserSecurityConfig extends AbstractChannelSecurityConfig {

	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
	
	@Autowired
	private ValidateCodeSecurityConfig validateCodeSecurityConfig;
	
	@Autowired
	private SpringSocialConfigurer yafeySocialSecurityConfig;
	
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 数据库创建语句 ： CREATE DATABASE `imooc_c134` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
//    	/** Default SQL for creating the database table to store the tokens */
//    	public static final String CREATE_TABLE_SQL = "create table persistent_logins (username varchar(64) not null, series varchar(64) primary key, "
//    			+ "token varchar(64) not null, last_used timestamp not null)";
          // 下面这条语句只能运行一次 ， 可以使用 上面的语句 创建数据库 和 表。
//        tokenRepository.setCreateTableOnStartup(true); // 数据库中如果没有表的话 就创建。
        return tokenRepository;
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		applyPasswordAuthenticationConfig(http);
		
		http.apply(validateCodeSecurityConfig)
				.and()
			.apply(smsCodeAuthenticationSecurityConfig)  // apply 方法 将 其他地方的配置加到这里来， 使之生效。
				.and()
			.apply(yafeySocialSecurityConfig) // 添加 social 的配置
				.and()
			.rememberMe()
				.tokenRepository(persistentTokenRepository())
				.tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
				.userDetailsService(userDetailsService)
				.and()
			//session相关的控制
			.sessionManagement()
				//指定session超时跳向的url
				.invalidSessionUrl("/session/invalid")
				//指定最大的session并发数量---即一个用户只能同时在一处登陆（腾讯视频的账号好像就只能同时允许2-3个手机同时登陆）
				.maximumSessions(1)
				//超过最大session并发数量时的策略
				.expiredSessionStrategy(new YaFeyExpiredSessionStrategy())
				.and()
				.and()
			.authorizeRequests() // 授权 ,除了不需要校验的，其他请求都需要校验
				.antMatchers(
					SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
					SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE,
					securityProperties.getBrowser().getLoginPage(),
					SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/*"
					,securityProperties.getBrowser().getSignUpUrl()
					,"/user/regist"
					,"/session/invalid"
						).permitAll()  //登陆页面 及 配置的 url 不需要校验
				.anyRequest()     //任何请求
				.authenticated()  //都需要身份认证
				.and()
			.csrf().disable()     // 关闭 跨站请求伪造 防护。
			;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
