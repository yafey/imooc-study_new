package com.imooc.security.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@EnableResourceServer //咦，没错还是一个注解
public class ImoocResourcesServerConfig {
}