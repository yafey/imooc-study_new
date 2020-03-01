package com.yafey.security.core.social.qq.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import com.yafey.security.core.social.qq.api.QQ;
import com.yafey.security.core.social.qq.api.QQUserInfo;

public class QQAdapter implements ApiAdapter<QQ> {

	/**
	 * 表示和 QQ 的连接是否 还通畅，我们这里直接返回 true。
	 */
	@Override
	public boolean test(QQ api) {
		return true;
	}

	/**
	 * 把 QQUserInfo （从 Api 接口获取的数据） 转换成 ConnectionValues 需要的数据。
	 */
	@Override
	public void setConnectionValues(QQ api, ConnectionValues values) {
		QQUserInfo userInfo = api.getUserInfo();
		
		values.setDisplayName(userInfo.getNickname());
		values.setImageUrl(userInfo.getFigureurl_qq_1());
		values.setProfileUrl(null); // qq 里面没有 个人主页。 比如 微博，这个就可以设置为 个人主页。
		values.setProviderUserId(userInfo.getOpenId()); // 用户在  服务提供商 那边的唯一标识。
	}

	// 跟 api.getUserInfo() ， 后续 绑定、解绑 的时候再写。
	@Override
	public UserProfile fetchUserProfile(QQ api) {
		// TODO Auto-generated method stub
		return null;
	}

	// 比如 微博，可以发一条消息去更新微博， 在 QQ 这里没有这种操作。
	@Override
	public void updateStatus(QQ api, String message) {
		//do noting
	}

}
