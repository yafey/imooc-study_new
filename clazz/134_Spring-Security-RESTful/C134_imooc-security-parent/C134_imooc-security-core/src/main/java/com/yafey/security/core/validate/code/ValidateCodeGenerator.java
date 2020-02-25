package com.yafey.security.core.validate.code;

import org.springframework.web.context.request.ServletWebRequest;

public interface ValidateCodeGenerator {

	ValidateCode generate(ServletWebRequest request);
	
}
