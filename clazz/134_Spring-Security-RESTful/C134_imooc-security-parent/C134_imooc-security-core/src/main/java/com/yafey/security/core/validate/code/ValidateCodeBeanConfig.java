package com.yafey.security.core.validate.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yafey.security.core.properties.SecurityProperties;
import com.yafey.security.core.validate.code.sms.DefaultSmsCodeSender;
import com.yafey.security.core.validate.code.sms.SmsCodeSender;

@Configuration
public class ValidateCodeBeanConfig {
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Bean
	@ConditionalOnMissingBean(name = "imageValidateCodeGenerator")  // 如果在 Spring 容器中没有找到 name 的bean，再初始化 bean。
	public ImageCodeGenerator imageValidateCodeGenerator() {
		ImageCodeGenerator codeGenerator = new ImageCodeGenerator(); 
		codeGenerator.setSecurityProperties(securityProperties);
		return codeGenerator;
	}
	
	@Bean
	@ConditionalOnMissingBean(SmsCodeSender.class)  // 如果在 Spring 容器中没有找到 bean，再初始化 bean。
	public SmsCodeSender smsCodeSender() {
		return new DefaultSmsCodeSender();
	}

}
