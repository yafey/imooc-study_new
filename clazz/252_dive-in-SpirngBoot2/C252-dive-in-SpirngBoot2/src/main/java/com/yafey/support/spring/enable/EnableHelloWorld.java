package com.yafey.support.spring.enable;

import java.lang.annotation.*;
import org.springframework.context.annotation.Import;
import com.yafey.support.spring.enable.interfaces.HelloWorldImportSelector;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
//@Import(HelloWorldConfiguration.class)//注解驱动实现方式
@Import(HelloWorldImportSelector.class)//接口编程实现方式
public @interface EnableHelloWorld {
}
