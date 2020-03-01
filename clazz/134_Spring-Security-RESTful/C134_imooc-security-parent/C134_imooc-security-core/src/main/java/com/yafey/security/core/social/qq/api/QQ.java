package com.yafey.security.core.social.qq.api;
public interface QQ {
	/**
	 * Api 主要是为了 获取用户信息，因此我们只需要 声明一个 获取用户信息的 接口。
	 * @return
	 */
    QQUserInfo getUserInfo();
}