package com.yafey.support.spring.enable.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloWorldConfiguration {
	
	@Bean
    public String helloWorld() { // 方法名即 Bean 名称
        return "Hello,World 2019";
    }
}
