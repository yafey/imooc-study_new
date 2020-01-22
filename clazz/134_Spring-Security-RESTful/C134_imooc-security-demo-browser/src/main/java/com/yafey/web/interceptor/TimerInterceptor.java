package com.yafey.web.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class TimerInterceptor implements HandlerInterceptor {

	// 每次请求前 调用
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("timerIntercepotr preHandle");
		
		HandlerMethod hm = ((HandlerMethod)handler);
		String controllerName = hm.getBean().getClass().getName();
		String method = hm.getMethod().getName();
		System.out.println(String.format("timerIntercepotr,get request process method:[%s] in controller:[%s]", method,controllerName));
		request.setAttribute("startTime", new Date().getTime());
		return true;  // 默认 return false , 不会调用 Controller 的方法， 这里可以根据实际情况 进行控制。
	}

	// 每次请求结束之后调用， 如果请求中抛出异常， 则不会调用。
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("timerIntercepotr postHandle");
		Long start = (Long) request.getAttribute("startTime");
		System.out.println("timerIntercepotr postHandle 耗时："+(new Date().getTime()-start));
	}

	// 每次请求结束之后， 不管有没有异常都会调用。 注意：如果 exception已经被 ExceptionHandler 处理， 那么 exception 将会是 null。
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("timerIntercepotr afterCompletion");
		Long start = (Long) request.getAttribute("startTime");
		System.out.println("timerIntercepotr afterCompletion 耗时："+(new Date().getTime()-start) );
		System.out.println("exception:["+ex+"]");
	}

}
