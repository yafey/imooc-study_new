package com.yafey.security.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "yafey.security")
public class SecurityProperties {
	
	private BrowserProperties browser = new BrowserProperties();
	private ValidateCodeProperties code = new ValidateCodeProperties();

}

