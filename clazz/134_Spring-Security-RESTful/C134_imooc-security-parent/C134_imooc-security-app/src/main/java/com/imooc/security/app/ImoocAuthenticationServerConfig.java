package com.imooc.security.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@Configuration
@EnableAuthorizationServer  //是的，没错，就这么一个注解
public class ImoocAuthenticationServerConfig {

}
