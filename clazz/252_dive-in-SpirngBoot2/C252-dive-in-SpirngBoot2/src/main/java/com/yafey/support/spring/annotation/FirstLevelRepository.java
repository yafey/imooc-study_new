package com.yafey.support.spring.annotation;
import org.springframework.stereotype.Repository;
import java.lang.annotation.*;
/**
 * 一级 {@link Repository @Repository}
 *
 * @author YaFey
 * @since 2019年4月30日
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repository
public @interface FirstLevelRepository {

    String value() default "";

}