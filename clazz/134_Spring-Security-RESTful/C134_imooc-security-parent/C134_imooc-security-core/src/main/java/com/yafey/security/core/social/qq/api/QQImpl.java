package com.yafey.security.core.social.qq.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
//每个人的token 都是不一样的，因此我们的QQImpl不能够是单例的。即 不能使用 @Component 注解。
public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {

	// 通过 access Token 去发请求 获取 openid 的路径
	private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";
	// 获取用户信息的路径
	private static final String URL_GET_USERINFO = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";

	private String appId;

	private String openId;

	private ObjectMapper objectMapper = new ObjectMapper();

	// 构造函数中，我们调用了父类的构造方法，最主要的是第二个参数`TokenStrategy.ACCESS_TOKEN_PARAMETER`，
	// 表示我们去获取 openID 的时候，**token 是放在`请求参数`中的，父类默认是放在`请求头`里面的，所以我们这里必须这么写一下。**
	public QQImpl(String accessToken, String appId) {
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);

		this.appId = appId;

		String url = String.format(URL_GET_OPENID, accessToken);
		String result = getRestTemplate().getForObject(url, String.class);

		System.out.println(result);

		this.openId = StringUtils.substringBetween(result, "\"openid\":\"", "\"}");
	}

	/**
	 * 这个方法使用 openId 和 appId，发起 rest 请求从QQ那里获取到用户的具体信息，然后封装成 QQUserInfo 对象。
	 */
	@Override
	public QQUserInfo getUserInfo() {

		String url = String.format(URL_GET_USERINFO, appId, openId);
		String result = getRestTemplate().getForObject(url, String.class);

		System.out.println(result);

		QQUserInfo userInfo = null;
		try {
			userInfo = objectMapper.readValue(result, QQUserInfo.class);
			userInfo.setOpenId(openId);
			return userInfo;
		} catch (Exception e) {
			throw new RuntimeException("获取用户信息失败", e);
		}
	}

}
