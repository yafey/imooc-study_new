package com.yafey.security.core.social.qq.connect;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QQOAuth2Template extends OAuth2Template {
	
	public QQOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
		// 默认为 false ， 只有设置为 true 的时候才会传 clientId 和 clientSecret。 
		// 详见 org.springframework.social.oauth2.OAuth2Template.authenticateClient(String)
		 setUseParametersForClientAuthentication(true); 
	}
	
	// 解析 QQ 的 response，详见 org.springframework.social.oauth2.OAuth2Template.postForAccessGrant(String, MultiValueMap<String, String>)
	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		 String responseStr = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
		 
		 log.info("获取accessToke的响应："+responseStr);
		 
		 String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(responseStr, "&");
		 
		 String accessToken = StringUtils.substringAfterLast(items[0], "=");
		 Long expiresIn = new Long(StringUtils.substringAfterLast(items[1], "="));
		 String refreshToken = StringUtils.substringAfterLast(items[2], "=");
		 
		 return new AccessGrant(accessToken, null, refreshToken, expiresIn);
	}
	
	// 增加 处理 content type 为 “text/html” 格式的 MessageConverter 
	@Override
	protected RestTemplate createRestTemplate() {
		 RestTemplate restTemplate = super.createRestTemplate();
		 restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		 return restTemplate;
	}

}
