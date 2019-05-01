package com.yafey.diveinspringboot.demo4repository;

import org.springframework.stereotype.Component;
import com.yafey.support.spring.annotation.FirstLevelRepository;
import com.yafey.support.spring.annotation.SecondLevelRepository;

/**
 *
 * @author YaFey
 * @since 2019年4月30日
 */
//@FirstLevelRepository(value = "myFirstLevelRepository")//value指定bean的名称
@Component(value = "myFirstLevelRepository")//同样的效果，注解的派生性
//@SecondLevelRepository(value = "myFirstLevelRepository") // 同样的效果，注解的层次性
public class MyFirstLevelRepository {

}
