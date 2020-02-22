package com.yafey.security.browser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查找用户信息
        log.info("用户是:" + username);
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
}

