---
typora-root-url: ./
typora-copy-images-to: ./README_images/4_and_7
---



# Spring Security 常见用法(第4章) 及 授权机制(第7章)

## 第4章 使用 Spring Security 开发基于表单的登录



### 4.2(4-2). Spring Security 的基本原理和核心概念

#### 4.2.1. Spring Security 核心概念

![image-20200214211553256](README_images/4_and_7/image-20200214211553256.png)

#### 4.2.2. 开箱即用的 Spring Security

当项目中（直接或间接）引入了 Spring Security ，<span style="color:green">**无需任何配置**</span>， **默认以 `Http Basic` 方式 进行校验**。

1. 重新打开之前关掉的 spring security 。（对于新项目来说，不用这一步。）

2. 访问任意接口，浏览器弹出的 对话框中输入 user/pwd 。

   a. 用户名：默认为 `user` ,  密码：在启动日志中查找 如下字段。

   ```txt
   Using default security password: fb0ba698-c091-4192-ae48-ffaa772ddddc
   ```

![image-20200214224714465](README_images/4_and_7/image-20200214224714465.png)



#### 4.2.3. 修改成 form 校验

在 C134_imooc-security-browser 中 新建 BrowserSecurityConfig 配置类。

<span style="color:green">**如果将 formLogin 改成 httpBasic 就是 Security 的 默认实现。**</span>

```java
package com.yafey.security.browser;

@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin()  // 认证方式
//		http.httpBasic()
			// 授权 , 以下表示 任何请求都需要 校验
			.and()
			.authorizeRequests()
			.anyRequest()
			.authenticated();
	}
}
```



启动 C134_imooc-security-demo-browser ， 访问 任意 服务，如 http://localhost:8080/ , **将会先跳转到 http://localhost:8080/login 进行校验，校验通过后再跳转到 原先的地址。**



![image-20200214232433403](README_images/4_and_7/image-20200214232433403.png)

![security-form-redirect.gif](README_images/4_and_7/security-form-redirect.gif)