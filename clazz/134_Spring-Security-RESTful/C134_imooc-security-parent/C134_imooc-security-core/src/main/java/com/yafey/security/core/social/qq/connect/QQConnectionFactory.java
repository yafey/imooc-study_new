package com.yafey.security.core.social.qq.connect;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

import com.yafey.security.core.social.qq.api.QQ;

public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

	// providerId 表示 服务提供商 的唯一标识 ，放在配置中。
	// appId 和 appSecret 也从外面传进来，交给 QQServiceProvider。 
	/**
	 * 将我们构建出的 QQServiceProvider 和 QQAdapter 传给 父类的构造函数即可。
	 */
	public QQConnectionFactory(String providerId, String appId, String appSecret) {
		super(providerId, new QQServiceProvider(appId, appSecret), new QQAdapter());
	}

}
