package com.yafey.security.core.properties;

import org.springframework.boot.autoconfigure.social.SocialProperties;

import lombok.Data;

// 父类中声明了 appid 和 appSecret
@Data
public class QQProperties extends SocialProperties {
	
	private String providerId = "qq";  // 标识符 就设置为 qq 就可以
}
