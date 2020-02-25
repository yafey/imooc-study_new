package com.yafey.security.core.validate.code.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.context.request.ServletWebRequest;

import com.yafey.security.core.validate.code.ValidateCode;
import com.yafey.security.core.validate.code.ValidateCodeGenerator;
import com.yafey.security.core.validate.code.ValidateCodeProcessor;

public abstract class AbstractValidateCodeProcessor<C extends ValidateCode> implements ValidateCodeProcessor {
	/**
	 * 操作session的工具类
	 */
	private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
	/**
	 * 收集系统中所有的 {@link ValidateCodeGenerator} 接口的实现。
	 */
	// image 和 sms 的 generator 都是 ValidateCodeGenerator 的 实现类， 
	// 当 Spring 在初始化的时候，看到 `Map<String, ValidateCodeGenerator>` 这样的结构，
	// 会查找所有 ValidateCodeGenerator 接口的 实现类，然后 将找到的 bean 以 bean name 为 key 放到 这个 Map 中。
	@Autowired
	private Map<String, ValidateCodeGenerator> validateCodeGenerators;

	/* (non-Javadoc)
	 * @see com.yafey.security.core.validate.code.ValidateCodeProcessor#create(org.springframework.web.context.request.ServletWebRequest)
	 */
	@Override
	public void create(ServletWebRequest request) throws Exception {
		C validateCode = generate(request);
		save(request, validateCode);
		send(request, validateCode);
	}
	/**
	 * 生成校验码
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private C generate(ServletWebRequest request) {
		String type = getProcessorType(request);
		ValidateCodeGenerator validateCodeGenerator = validateCodeGenerators.get(type+"CodeGenerator");
		return (C) validateCodeGenerator.generate(request);
	}
	/**
	 * 保存校验码
	 * @param request
	 * @param validateCode
	 */
	private void save(ServletWebRequest request, C validateCode) {
		sessionStrategy.setAttribute(request, SESSION_KEY_PREFIX + getProcessorType(request).toUpperCase(),
				validateCode);
	}
	/**
	 * 发送校验码，由子类实现
	 * @param request
	 * @param validateCode
	 * @throws Exception
	 */
	protected abstract void send(ServletWebRequest request, C validateCode) throws Exception;
	/**
	 * 根据请求的url获取校验码的类型
	 * @param request
	 * @return
	 */
	private String getProcessorType(ServletWebRequest request) {
		return StringUtils.substringAfter(request.getRequest().getRequestURI(), "/code/");
	}
}
