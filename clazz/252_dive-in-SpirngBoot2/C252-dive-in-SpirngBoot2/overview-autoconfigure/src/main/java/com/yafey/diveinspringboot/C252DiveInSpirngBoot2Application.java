package com.yafey.diveinspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class C252DiveInSpirngBoot2Application {

	public static void main(String[] args) {
		// SpringApplication.run(C252DiveInSpirngBoot2Application.class, args);
		 new SpringApplicationBuilder(C252DiveInSpirngBoot2Application.class).run(args);
		// 上面的 这两种写法是等价的。
		
		// Fluter API 使用示例
//		new SpringApplicationBuilder(C252DiveInSpirngBoot2Application.class)
//		.web(WebApplicationType.NONE) // Fluter API, 指定应用为 非 Web 应用。
//		.properties("abc=def") // Fluter API, 设置属性 abc
//		.run(args);
	}
}
