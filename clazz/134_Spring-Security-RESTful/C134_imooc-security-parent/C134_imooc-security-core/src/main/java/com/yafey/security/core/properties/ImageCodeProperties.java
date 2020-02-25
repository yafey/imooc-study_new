package com.yafey.security.core.properties;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ImageCodeProperties extends SmsCodeProperties{
	
	private int width = 67;
	private int height = 23;
	
	public ImageCodeProperties() {
		setLength(4);
	}
	
}
