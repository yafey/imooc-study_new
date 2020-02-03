package com.yafey.web.async2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig4AsynchInterceptor extends WebMvcConfigurerAdapter {
	
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		// 注册 CallableProcessingInterceptor
//		configurer.registerCallableInterceptors(new CallableProcessingInterceptor[] {});
		// 注册 DeferredResultProcessingInterceptor
//		configurer.registerDeferredResultInterceptors(new DeferredResultProcessingInterceptor[] {});
		
		// Spring 默认使用 SimpleAsyncTaskExecutor , 这个“异步线程池” 不会重用 线程， 生产上建议使用自定义的线程池。
		// By default a SimpleAsyncTaskExecutor instance is used, and it'shighly recommended to change that default in production since the simpleexecutor does not re-use threads.
//		configurer.setTaskExecutor(taskExecutor);
	}

}