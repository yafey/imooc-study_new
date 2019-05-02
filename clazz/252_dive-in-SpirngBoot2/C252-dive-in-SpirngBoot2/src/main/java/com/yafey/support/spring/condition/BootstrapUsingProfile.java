package com.yafey.support.spring.condition;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.yafey.support.spring.condition.profile.CalculateService;

@SpringBootApplication(scanBasePackages = { "com.yafey.support.spring.condition" })
public class BootstrapUsingProfile {
	public static void main(String[] args) {
		//@formatter:off
		ConfigurableApplicationContext context = new SpringApplicationBuilder(BootstrapUsingProfile.class)
				.web(WebApplicationType.NONE)
				// 这里可以灵活配置 java7 或 java8 , 但不能同时配置两个。
				.profiles("java8")  // 在启动时没有配置.profiles()，直接报错 NoUniqueBeanDefinitionException: No qualifying bean of type '......CalculateService' available
				.run(args);
		//@formatter:on

		CalculateService service = context.getBean(CalculateService.class);
		System.out.println("CalculateService.sum(1...10):" + service.sum(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

		// 关闭上下文
		context.close();
	}
}
