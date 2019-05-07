package com.yafey.diveinspringboot.demo4repository;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

// 这里的 basePackages 传递的是 自定义注解 所在的 package
@ComponentScan(basePackages="com.yafey.diveinspringboot.demo4repository")

// Enable注解 需要配合 spring.factories , 后续说明， 或参考：https://www.jianshu.com/p/464d04c36fb1
//@EnableAutoConfiguration
public class RepositoryBootstrap {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(RepositoryBootstrap.class)
		//非web工程
        .web(WebApplicationType.NONE)
        .run(args);
		
		//检查 bean 是否存在 , 名为：myFirstLevelRepository 
		MyFirstLevelRepository bean = ctx.getBean("myFirstLevelRepository", MyFirstLevelRepository.class);
        System.out.println("myFirstLevelRepository"+bean);
        
        //关闭上下文
        ctx.close();		
	}

}
