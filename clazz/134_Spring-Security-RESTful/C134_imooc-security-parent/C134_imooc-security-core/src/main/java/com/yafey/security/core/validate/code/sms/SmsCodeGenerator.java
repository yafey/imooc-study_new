package com.yafey.security.core.validate.code.sms;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import com.yafey.security.core.properties.SecurityProperties;
import com.yafey.security.core.validate.code.ValidateCode;
import com.yafey.security.core.validate.code.ValidateCodeGenerator;

import lombok.Getter;
import lombok.Setter;

@Component("smsValidateCodeGenerator")
public class SmsCodeGenerator implements ValidateCodeGenerator {

	/**
	 * 系统配置
	 */
	@Autowired @Setter @Getter
	private SecurityProperties securityProperties;
	
	@Override
	public ValidateCode generate(ServletWebRequest request) {
		String code = RandomStringUtils.randomNumeric(securityProperties.getCode().getSms().getLength());
		return new ValidateCode(code, securityProperties.getCode().getSms().getExpireIn());
	}

}
