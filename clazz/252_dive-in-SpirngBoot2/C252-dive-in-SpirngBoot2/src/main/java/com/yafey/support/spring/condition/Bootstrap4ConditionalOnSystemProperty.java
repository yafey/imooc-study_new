package com.yafey.support.spring.condition;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 基于`编程方式` 实现的 自定义 条件装配 引导类 （基于 系统属性）
 */
public class Bootstrap4ConditionalOnSystemProperty {

	@Bean
	@ConditionalOnSystemProperty(name = "user.country", value = "~CN")
	public String helloWorld() {
		return "hello world.";
	}

	public static void main(String[] args) {
		//@formatter:off
		System.getProperties().keySet().stream().forEach(f-> System.out.println(f +"="+System.getProperty(""+f)));
		ConfigurableApplicationContext context = new SpringApplicationBuilder(Bootstrap4ConditionalOnSystemProperty.class)
				.web(WebApplicationType.NONE)
				.run(args);
		//@formatter:on

		String helloWorld = context.getBean("helloWorld", String.class);
		System.out.println(helloWorld);

		// 关闭上下文
		context.close();
	}
}