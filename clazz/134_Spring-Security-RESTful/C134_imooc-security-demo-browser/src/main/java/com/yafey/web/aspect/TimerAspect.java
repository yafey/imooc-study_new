package com.yafey.web.aspect;

import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
public class TimerAspect {

//	@Before
//	@After
//	@AfterThrowing
//	@Around  // 覆盖了前面 3 种。
	
	// 切入点表达式语法 参见 Spring 官网 （Spring Framework --> Reference --> <key words: aspect> ）
	// https://docs.spring.io/spring/docs/5.2.3.RELEASE/spring-framework-reference/core.html#aop-common-pointcuts
    @Around("execution(* com.yafey.web.controller..*(..))")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
    	System.out.println("TimeAspect start");

    	System.out.println(String.format("args: %s", ArrayUtils.toString(pjp.getArgs()) ) );

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        
    	System.out.println(String.format("TimeAspect 耗时：{%s} 毫秒", System.currentTimeMillis() - start ) );

        System.out.println("TimeAspect end");

        return result;
    }
	
	
}
