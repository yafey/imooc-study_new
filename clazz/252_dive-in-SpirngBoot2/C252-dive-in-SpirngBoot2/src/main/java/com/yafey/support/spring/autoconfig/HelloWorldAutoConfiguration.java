package com.yafey.support.spring.autoconfig;

import com.yafey.support.spring.condition.ConditionalOnSystemProperty;
import com.yafey.support.spring.enable.EnableHelloWorld;
import com.yafey.support.spring.enable.annotation.HelloWorldConfiguration;

/**
 * 实现自动装配， 这里整合了 多种自动装配的底层实现方式: （实际使用时，只需要选择其中之一即可。这里的示例如果只选用 Spring 模式注解 ， 会报错，因为 bean 不在这里声明。 ）<br/>
 * <ol>
 * <li>Spring 条件装配： <code>@ConditionalOnSystemProperty</code>,user.country == "CN"</li>
 * <li>Spring 模式注解 装配： <code>@Configuration</code> 。 <br/>因为不能重复定义 bean, bean 和 <code>@Configuration</code> 其实体现在   {@link HelloWorldConfiguration} 类上 </li>
 * <li>Spring <code>@Enable 模块</code> 装配： (当前提交的代码 将使用 <code>@Enable 模块</code>装配的 接口编程实现方式  ) <br/> 
 * 	加载顺序：<code>@EnableHelloWorld 注解  加载 -->HelloWorldImportSelector 返回 --> HelloWorldConfiguration 生成 bean --> helloWorld</code>
 * </li>
 * </ol>
 */
//@Configuration // Spring 模式注解 
@EnableHelloWorld // Spring @Enable 模块装配
@ConditionalOnSystemProperty(name = "user.country", value = "CN") // 条件装装配
public class HelloWorldAutoConfiguration {
}
