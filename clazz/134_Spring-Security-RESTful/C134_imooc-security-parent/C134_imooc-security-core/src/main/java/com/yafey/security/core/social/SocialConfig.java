package com.yafey.security.core.social;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.web.servlet.View;

import com.imooc.security.core.social.SocialAuthenticationFilterPostProcessor;
import com.yafey.security.core.properties.SecurityProperties;
import com.yafey.security.core.social.jdbc.YafeyJdbcUsersConnectionRepository;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@EnableSocial  // 声明 使用 Spring Social
public class SocialConfig extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired(required = false)
	private ConnectionSignUp connectionSignUp;
	
	//设置springsocial成功处理器相关的类
    @Autowired(required = false)
    private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		/**
         * 第二个参数的作用：根据条件查找该用哪个ConnectionFactory来构建Connection对象
         * 第三个参数的作用: 对插入到userconnection表中的数据进行加密和解密
         */
		YafeyJdbcUsersConnectionRepository repository = new YafeyJdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator, Encryptors.noOpText());
		repository.setTablePrefix("yafey_");  // 假设我们的 表名 规范里面有统一的前缀。
		if(connectionSignUp != null) {
			repository.setConnectionSignUp(connectionSignUp);
		}
		return repository;
	}
	
    /**
     * 通过apply()该实例，可以将SocialAuthenticationFilter加入到spring-security的过滤器链
     */
	@Bean
	public SpringSocialConfigurer yafeySocialSecurityConfig() {
		// 默认配置类，进行组件的组装
        // 包括了过滤器SocialAuthenticationFilter 添加到security过滤链中
		String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
		YafeySpringSocialConfigurer configurer = new YafeySpringSocialConfigurer(filterProcessesUrl);
		
        //指定SpringSocial/SpringSecurity跳向注册页面时的url为我们配置的url
		configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl()); // 当找不到用户时，跳转到 注册页面。
		
		//设置springsocial的认证成功处理器 -- app下可以返回token，browser下使用spring-security默认的
        configurer.setSocialAuthenticationFilterPostProcessor(socialAuthenticationFilterPostProcessor);
		return configurer;
	}
	

	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
		return new ProviderSignInUtils(connectionFactoryLocator,
				getUsersConnectionRepository(connectionFactoryLocator)) {
		};
	}
	
	// connect/weixinConnected : 绑定。
	// connect/weixinConnect : 解绑。
	@Bean({"connect/weixinConnect", "connect/weixinConnected"})
	@ConditionalOnMissingBean(name = "weixinConnectedView")
	public View weixinConnectedView() {
		return new YaFeyConnectView();
	}

}