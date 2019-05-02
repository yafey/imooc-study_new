package com.yafey.support.spring.condition.profile;

import java.util.stream.Stream;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * java8 lambda 表达式 实现 {@link CalculateService}
 */
@Profile("java8")
@Service
public class Java8CalculateService implements CalculateService {

	@Override
	public Integer sum(Integer... values) {
		System.out.println("java8 lambda 表达式 实现");
		int sum = Stream.of(values).reduce(0, Integer::sum);
		return sum;
	}
}