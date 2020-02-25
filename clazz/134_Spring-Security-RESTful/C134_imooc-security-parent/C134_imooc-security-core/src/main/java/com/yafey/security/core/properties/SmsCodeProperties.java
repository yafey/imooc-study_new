package com.yafey.security.core.properties;

import lombok.Data;

@Data
public class SmsCodeProperties {
	
	private int length = 6;
	private int expireIn = 60;
	
	private String url; // 逗号分隔的字符串，如 "/user,/user/*"
	
}
