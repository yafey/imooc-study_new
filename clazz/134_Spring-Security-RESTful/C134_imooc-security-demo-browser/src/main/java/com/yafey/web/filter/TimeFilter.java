package com.yafey.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;

//要使过滤器生效，非常简单，只需将它标记为 Component 。
@Component  
public class TimeFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("timefilter init");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("timefilter start");
		long start = System.currentTimeMillis();
		System.out.println("timefilter request:"+ReflectionToStringBuilder.toString(request, ToStringStyle.MULTI_LINE_STYLE) );
		chain.doFilter(request, response);
		System.out.println("timefilter 耗时："+(System.currentTimeMillis()-start));
		System.out.println("timefilter finished");
	}

	@Override
	public void destroy() {
		System.out.println("timefilter destroy");
	}

}
