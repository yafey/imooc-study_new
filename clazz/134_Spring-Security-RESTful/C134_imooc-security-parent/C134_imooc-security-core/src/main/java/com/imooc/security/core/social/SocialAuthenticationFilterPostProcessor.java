package com.imooc.security.core.social;

import org.springframework.social.security.SocialAuthenticationFilter;

/**
 * Description：指定springsocial成功处理器的接口
 */
public interface SocialAuthenticationFilterPostProcessor {

    /**
     * 参数为springsocial的过滤器
     * @param socialAuthenticationFilter
     */
    void process(SocialAuthenticationFilter socialAuthenticationFilter);
}