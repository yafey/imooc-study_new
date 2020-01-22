package com.yafey.web.filter.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yafey.web.filter._3rdPartyFilter;

@Configuration
public class WebConfig4Filter {
	
	@Bean
	public FilterRegistrationBean timeFilter() {
		
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		
		_3rdPartyFilter _3rdPartyFilter = new _3rdPartyFilter();
		registrationBean.setFilter(_3rdPartyFilter);
		 
		List<String> urls = new ArrayList<>();
		urls.add("/*");  // 此处表示 拦截所有的请求。
		registrationBean.setUrlPatterns(urls);
		
		return registrationBean;
		
	}

}