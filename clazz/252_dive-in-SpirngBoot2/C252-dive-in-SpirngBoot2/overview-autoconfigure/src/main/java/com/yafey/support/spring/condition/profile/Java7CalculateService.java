package com.yafey.support.spring.condition.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * java7 for 循环 实现 {@link CalculateService}
 */
@Profile("java7")
@Service
public class Java7CalculateService implements CalculateService {

	@Override
	public Integer sum(Integer... values) {
		System.out.println("java7 for 循环 实现");
		int sum = 0;
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return sum;
	}

}