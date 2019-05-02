package com.yafey.support.spring.condition.profile;

/**
 * 计算服务
 */
public interface CalculateService {

	/**
	 * 多个整数求和
	 * 
	 * @param values
	 * @return sum 累加值
	 */
	public Integer sum(Integer... values);
}
