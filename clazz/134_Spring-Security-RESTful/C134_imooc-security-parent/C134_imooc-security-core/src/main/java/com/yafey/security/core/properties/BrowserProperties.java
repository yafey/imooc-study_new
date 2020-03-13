package com.yafey.security.core.properties;

import lombok.Data;

@Data
public class BrowserProperties {
	
	private String loginPage = SecurityConstants.DEFAULT_LOGIN_PAGE_URL;
	private LoginResponseType loginType = LoginResponseType.JSON;
	
	private int rememberMeSeconds = 3600;
	
	private String signUpUrl = "/yafey-signUp.html";
	
	private SessionProperties session = new SessionProperties();
	
	private String signOutUrl="/";

}
