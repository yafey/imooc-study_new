package com.yafey.security.browser;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.yafey.security.core.properties.SecurityProperties;
import com.yafey.security.core.validate.code.ValidateCodeFilter;

@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;
	
    @Autowired
    private AuthenticationSuccessHandler yafeyAuthentivationSuccessHandler;

    @Autowired
    private YafeyAuthentivationFailureHandler yafeyAuthentivationFailureHandler;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private UserDetailsService userDetailsService;

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
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        // 设置 filter 的失败 处理器
        validateCodeFilter.setAuthenticationFailureHandler(yafeyAuthentivationFailureHandler)
        					.setSecurityProperties(securityProperties)
        					.afterPropertiesSet(); // 配置初始化
        
		http
        	.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class) // 将 filter 加在 某个过滤器 之前
			.formLogin()  // 认证方式
				.loginPage("/authentication/require") // 自定义 登陆页面
		        .loginProcessingUrl("/authentication/form") // 自定义表单 处理请求，伪造的请求
		        .successHandler(yafeyAuthentivationSuccessHandler)  // 配置成功处理器
		        .failureHandler(yafeyAuthentivationFailureHandler)  // 配置失败处理器
	        .and()
            .rememberMe()  // 调用 rememberMe 方法 并进行配置
	            .tokenRepository(persistentTokenRepository())
	            .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSecond())
	            .userDetailsService(userDetailsService)
//		http.httpBasic()
	         // 授权 , 以下表示 任何请求都需要 校验
	         .and()
	         .authorizeRequests()//对请求进行授权
	         .antMatchers("/authentication/require",
	        		 securityProperties.getBrowser().getLoginPage()
//	        		 ,"/code/image"
//	        		 ,"/code/sms"
	        		 ,"/code/*"
	        		 ).permitAll() //登陆页面不需要校验
	         .anyRequest()//任何请求
	         .authenticated()//都需要身份认证
	         .and()
	         .csrf().disable() // 关闭 跨站请求伪造 防护。
	         ;  
	}
}
