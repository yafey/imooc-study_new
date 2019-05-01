package com.yafey.support.spring.annotation;

import java.lang.annotation.*;

/**
 * @author YaFey
 * @since 2019年4月30日
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FirstLevelRepository
public @interface SecondLevelRepository {

    String value() default "";

}