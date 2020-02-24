package com.yafey.security.core.properties;

import lombok.Data;

@Data
public class ImageCodeProperties {
	
	private int width = 67;
	private int height = 23;
	private int length = 4;
	private int expireIn = 60;
	
	private String url; // 逗号分隔的字符串，如 "/user,/user/*"
	
}
