package com.yafey.security.browser.session;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 如果设置的session并发策略为一个账户第二次登陆会将第一次给踢下来
 * 则第一次登陆的用户再访问我们的项目时会进入到该类
 * event里封装了request、response信息
 */
@Slf4j
public class YaFeyExpiredSessionStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        String header = event.getRequest().getHeader("user-agent");
        log.info("浏览器信息为：{}", header);

        //告诉前端并发登陆异常
        event.getResponse().setContentType("application/json;charset=UTF-8");
        event.getResponse().getWriter().write("并发登陆！！！<br/>"+header);
    }
}