package com.yafey.security.core.properties;

import lombok.Data;

@Data
public class BrowserProperties {
	
	private String loginPage = "/self-login.html";
	private LoginResponseType loginType = LoginResponseType.JSON;
	
	private int rememberMeSeconds = 3600;

}
