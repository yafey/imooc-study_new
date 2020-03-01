package com.yafey.security.core.social;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SpringSocialConfigurer;

import com.yafey.security.core.properties.SecurityProperties;

@Configuration
@EnableSocial  // 声明 使用 Spring Social
public class SocialConfig extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private SecurityProperties securityProperties;
	
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator, Encryptors.noOpText());
		repository.setTablePrefix("yafey_");  // 假设我们的 表名 规范里面有统一的前缀。
		return repository;
	}
	
	@Bean
	public SpringSocialConfigurer yafeySocialSecurityConfig() {
		String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
		YafeySpringSocialConfigurer configurer = new YafeySpringSocialConfigurer(filterProcessesUrl);
		configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl()); // 当找不到用户时，跳转到 注册页面。
		return configurer;
	}
	
	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
		return new ProviderSignInUtils(connectionFactoryLocator,
				getUsersConnectionRepository(connectionFactoryLocator)) {
		};
	}

}