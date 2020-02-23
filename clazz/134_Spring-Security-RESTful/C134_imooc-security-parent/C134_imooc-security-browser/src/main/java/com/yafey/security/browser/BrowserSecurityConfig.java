package com.yafey.security.browser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.yafey.security.core.properties.SecurityProperties;

@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;
	
    @Autowired
    private AuthenticationSuccessHandler yafeyAuthentivationSuccessHandler;

    @Autowired
    private YafeyAuthentivationFailureHandler yafeyAuthentivationFailureHandler;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin()  // 认证方式
			.loginPage("/authentication/require") // 自定义 登陆页面
	        .loginProcessingUrl("/authentication/form") // 自定义表单 处理请求，伪造的请求
	        .successHandler(yafeyAuthentivationSuccessHandler)  // 配置成功处理器
	        .failureHandler(yafeyAuthentivationFailureHandler)  // 配置失败处理器
//		http.httpBasic()
	         // 授权 , 以下表示 任何请求都需要 校验
	         .and()
	         .authorizeRequests()//对请求进行授权
	         .antMatchers("/authentication/require",
	        		 securityProperties.getBrowser().getLoginPage() 
	        		 ).permitAll() //登陆页面不需要校验
	         .anyRequest()//任何请求
	         .authenticated()//都需要身份认证
	         .and()
	         .csrf().disable() // 关闭 跨站请求伪造 防护。
	         ;  
	}
}
