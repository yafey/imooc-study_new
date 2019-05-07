package com.yafey.support.spring.condition;

import java.util.Map;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 实现 Condition <br/>
 * 实现方法 matches 返回 true 或 false
 */
public class OnSystemPropertyConditional implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

		// 获取注解的属性
		Map<String, Object> map = metadata.getAnnotationAttributes(ConditionalOnSystemProperty.class.getName());

		String propertyName = (String) map.get("name");
		String propertyValue = (String) map.get("value");

		String javaPropertyValue = System.getProperty(propertyName);

		System.out.println("javaPropertyValue:" + javaPropertyValue);
		System.out.println("propertyValue:" + propertyValue);

		return propertyValue.equals(javaPropertyValue);
	}
}