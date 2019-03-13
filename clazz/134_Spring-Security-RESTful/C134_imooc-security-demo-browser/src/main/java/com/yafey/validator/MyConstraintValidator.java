package com.yafey.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.yafey.validator.service4demo.HelloService;

// 自定义的 校验器
//
// ConstraintValidator<A,T> 
//	泛型 A 表示 要验证的注解， 
//	泛型 T 表示要验证的对象是什么类型，如果只验证 字符串， 可以 写 String，那么 我们自定义的注解 @MyConstraint 只能放在 String 类型的字段上 。
public class MyConstraintValidator implements ConstraintValidator<MyConstraint, Object> {

	// 因为 implements 了 ConstraintValidator 接口， 不用在 自定义的校验器上用 @Component 或 @Service 标注，
	// Spring 会自动将 自定义的 校验器 作为 bean 来管理， 自动注入 Spring 容器中的 其他 bean。
	@Autowired
	private HelloService helloService;
	
	@Override
	public void initialize(MyConstraint constraintAnnotation) {
		// 校验器初始化时要做的动作。
		System.out.println("MyConstraintValidator init...");
		
	}

	/**
	 * Object value ： 要校验的数据。
	 * context ： 校验的上下文，包含了 自定义注解 @MyConstraint 里面的 一些值。
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
        System.out.println(String.format("传递给自定义校验器 MyConstraintValidator 的值 isValid value=[%s]", value));
        helloService.greeting("校验器的校验逻辑中可以调用 Spring 容器中的任何东西，比如这里是调用 Service 的方法。");
        // 返回 true 表示校验通过， false 表示校验失败。
		return false;
	}

}
