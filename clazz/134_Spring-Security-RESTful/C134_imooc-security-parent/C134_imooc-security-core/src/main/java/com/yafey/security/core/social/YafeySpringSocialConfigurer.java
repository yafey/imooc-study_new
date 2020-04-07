package com.yafey.security.core.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

import com.imooc.security.core.social.SocialAuthenticationFilterPostProcessor;

import lombok.Data;

@Data
public class YafeySpringSocialConfigurer extends SpringSocialConfigurer {
	
	private String filterProcessesUrl;
    //注入成功处理器处理器 -- 为null时走spring-security默认的，否则走传递过来的，这里也就是指可以发送token的
    private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;
	
	public YafeySpringSocialConfigurer(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(filterProcessesUrl);
		//如果配置了后置成功处理器，就将其传入
        if(socialAuthenticationFilterPostProcessor!=null){
            socialAuthenticationFilterPostProcessor.process(filter);
        }
		return (T) filter;
	}

}
