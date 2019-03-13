package com.yafey.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

// Target 表示注解可以标注在什么元素上，比如 我们自定义的这个注解，可以标注在 方法 和 字段上。 
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
// 用 @Constraint 注解标注我们自定义的注解，来表明我们的这个注解是一个 校验注解。
// validatedBy 表示 由什么类来处理我们的校验逻辑。
@Constraint(validatedBy = MyConstraintValidator.class)
public @interface MyConstraint {
	// 如果 自定义的注解 要想用作 像 Hibernate Validator 中那样的注解（如 @NotBlank / @Past）, 必须至少包含下面 3 个属性。
//	String message() default "{org.hibernate.validator.constraints.NotBlank.message}";
//	Class<?>[] groups() default { };
//	Class<? extends Payload>[] payload() default { };

	// 校验失败的提示信息， 这里就不 示范 默认值了（如果没有默认值，那么在使用时必须 给 message 属性赋值）。
	String message() ;
	// 下面 2 个注解 是 Hibernate Validator 里面的概念，这里暂时没用到。
	Class<?>[] groups() default { };
	Class<? extends Payload>[] payload() default { };

	
}
