package com.yafey.security.browser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyUserDetailsService implements UserDetailsService,SocialUserDetailsService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查找用户信息
        log.info("表单登陆 用户是:" + username);
        String password=passwordEncoder.encode("123qwe"); // 随机加盐加密， 每次的密码都不一样。
        log.info("数据库密码是：{}",password);
        //参数：用户名，密码，权限集合
        User user = new User(username, password,
        		AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
//        User user = new User(username, "123qwe",
//        		true,true,true,false,//该账户被锁定
//        		AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        return user;
    }

    // 这个方法是 Spring Social 根据 providerId 和 providerUserId 从 yafey_UserConnection 拿到 userId，然后传过来。
    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
    	log.info("社交登录用户Id:" + userId);
        return buildUser(userId);
    }

    private SocialUserDetails buildUser(String userId) {
        // 根据用户名查找用户信息
        //根据查找到的用户信息判断用户是否被冻结
        String password = passwordEncoder.encode("123456");
        log.info("数据库密码是:"+password);
        return new SocialUser(userId, password,
                true, true, true, true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}

