package com.yafey.security.core.social.qq.connect;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import com.yafey.security.core.social.qq.api.QQ;
import com.yafey.security.core.social.qq.api.QQImpl;

public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {

	private String appId; // 整个应用 对于 QQ 来说，有一个固定的 appID。
	
	private static final String URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";
	
	private static final String URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";
	
	public QQServiceProvider(String appId, String appSecret) {
		// 其中 appId 和 appSecret 是QQ分配给我们的。因为每个调用我们模块的这两个参数都不一样，所以需要传进来。
		// URL_AUTHORIZE 表示我们引导用户跳转的授权页面，对应步骤1。
		// URL_ACCESS_TOKEN 对应步骤4，表示去获取 token。
		super(new  QQOAuth2Template(appId, appSecret, URL_AUTHORIZE, URL_ACCESS_TOKEN));
		this.appId = appId;
	}
	
	@Override
	public QQ getApi(String accessToken) {
		return new QQImpl(accessToken, appId);
	}

}
