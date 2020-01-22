package com.yafey.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig4Interceptor extends WebMvcConfigurerAdapter {
	
	@Autowired
	private TimerInterceptor timerInterceptor;
	
	// 覆盖 WebMvcConfigurerAdapter#addInterceptors 方法， 将 拦截器 set 进去。
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(timerInterceptor);
	}

}