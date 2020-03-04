package com.yafey.security.core.properties;

import lombok.Data;

@Data
public class SocialProperties {
	private String filterProcessesUrl = "/auth";

	private QQProperties qq = new QQProperties();
	
	private WeixinProperties weixin = new WeixinProperties();
}
