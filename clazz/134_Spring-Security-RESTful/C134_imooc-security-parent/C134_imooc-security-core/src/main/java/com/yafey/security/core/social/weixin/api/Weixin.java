package com.yafey.security.core.social.weixin.api;

/**
 * 微信API调用接口
 */
public interface Weixin {

	/* (non-Javadoc)
	 * @see com.yafey.security.social.api.SocialUserProfileService#getUserProfile(java.lang.String)
	 */
	WeixinUserInfo getUserInfo(String openId);
	
}
