package com.yafey.support.spring.autoconfig;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 自动装配引导类 {@link HelloWorldAutoConfiguration}
 */
@EnableAutoConfiguration // 激活自动装配
public class Bootstrap4EnableAutoConfiguration {

	public static void main(String[] args) {
		//@formatter:off
		ConfigurableApplicationContext context = new SpringApplicationBuilder(Bootstrap4EnableAutoConfiguration.class)
				.web(WebApplicationType.NONE)
				.run(args);
		//@formatter:on

		String helloWorld = context.getBean("helloWorld", String.class);
		System.out.println(helloWorld);

		// 关闭上下文
		context.close();
	}
}