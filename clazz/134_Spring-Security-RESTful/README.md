---
typora-root-url: ./
typora-copy-images-to: ./README_images/
---


# 2 准备工作

## 2-1 开发环境搭建

JDK：1.8

STS （Spring Tools Suite）: https://spring.io/tools/sts

MySQL （MySQL Community Server）:  https://dev.mysql.com/downloads/mysql/  , 建议： windows 上使用 安装包（因为有 VS 的依赖）。

-  「MySQL Workbench」是 MySQL 官方客户端。



## 2-2 架构介绍



- 在 parent 项目中 ，配置 `<dependencyManagement>`  引入 pom 文件， 管理 maven 依赖（版本）。

- 设置 Project 级别的默认编译为 jdk-1.8  ， 也可以 设置成 maven 级别。（maven 默认编译级别为 jdk-1.5， 如果 project 在 Eclipse 中报错， 可通过 `Maven-->Update` 进行刷新。）

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-compiler-plugin</artifactId>
            <!-- version 不用指定 -->
			<configuration>
				<source>1.8</source>
				<target>1.8</target>
				<encoding>UTF-8</encoding>
			</configuration>
		</plugin>
	</plugins>
</build>
```

### 依赖 jar 说明

> 所有 jar 配置在 core 项目中， 其他项目只要 引入 core 项目 即可。
>
> > browser 项目需要额外引入 `spring-session` (支持 集群 session 管理) 。

```bat

# 主要引入 Spring-Security 和 Spring-Security-oauth2 。
spring-cloud-starter-oauth2

# 存储， Token 及 用户社交账号的绑定等。 
spring-boot-starter-data-redis
spring-boot-starter-jdbc
mysql-connector-java

# Spring-social 相关， 第三方 登录（如 QQ/微信）
spring-social-config
spring-social-core
spring-social-security
spring-social-web

# 工具包 ， 字符、集合、反射 操作。
commons-lang
commons-collections
commons-beanutils
```

## 2-3 开发 Hello Spring Security

### 问题记录

```properties
# Exception1 (Exp1)
Cannot determine embedded database driver class for database type NONE

# Reason1 (RS1) : 原因是没有配置数据库

# Exp2
Establishing SSL connection without server's identity verification is not recommended. According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be established by default if explicit option isn't set. For compliance with existing applications not using SSL the verifyServerCertificate property is set to 'false'. You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true and provide truststore for server certificate verification.

# RS2: jdbc url 后面增加参数 useSSL=false 即可
# RS1 和 RS2 配置如下
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/imooc-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=123456

# Exp3
No Spring Session store is configured: set the 'spring.session.store-type' property

# RS3: session 集群配置先 关闭， 另外还可以是 jdbc 和 redis。
spring.session.store-type=none

# Exp4 (参考：https://github.com/imooc-java/security ，实际没有遇到)
org.springframework.data.redis.RedisConnectionFailureException: Cannot get Jedis connection; nested exception is redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool

# RS4: 需要安装并配置 redis 服务器参数
spring.redis.host=127.0.0.1
spring.redis.port=6379

# Exp5:需要进行身份验证
# RS5 关闭 默认的 HTTP Basic 的认证。
security.basic.enabled=false
```



# 1-1 导学

## 有一定经验的程序员如何提升自己

![1552212951462](README_images/1552212951462.png)

* 每天都很忙，但感觉水平没有提升
* 不知道学什么，遇到复杂业务场景时，又感觉技术储备不够
* 工作中写过很多代码，但是面试时几句话就说完了

## 讲一门聚焦的课

![1552213074969](README_images/1552213074969.png)

* 把一个知识点所有常见场景和特性都覆盖到
* 不止讲自己写的代码，还要讲框架的源码
* 不止要实现功能，还要封装起来能重用，能给别人用

## 企业级的认证和授权

![1552213490833](README_images/1552213490833.png)



* 支持多种认证方式

  * 用户名/密码
  * QQ登陆
  * 微信登陆
  * ...

* 支持多种前端渠道
  * 浏览器
  * APP

* 支持集群环境

  * 跨应用工作
  * SESSION控制
  * 控制用户权限
  * 防护与身份认证相关的攻击



## 课程目标

![1552211501609](README_images/1552211501609.png)

* 深入理解 Spring Security 及相关框架的远离、功能和代码
* 可以基于 Spring Security 及相关框架独立开发认证授权相关功能
* 掌握抽象和封装的常见技巧可以编写可重用的模块供他人使用

# 目录

![1552211296835](README_images/1552211296835.png)



![1552219845132](README_images/1552219845132.png)

- 第2章 开始开发

  - 安装开发工具
  - 介绍项目代码结构并搭建，基本的依赖和参数设置
  - 开发 Hello Spring Security 。

- 第3章 使用 Spring MVC 开发 RESTful API
  - 认证授权模块 主要是为 一些服务接口（API） 提供 安全保护， 不被随意访问。
  - 开发 RESTful 风格的 基本的增删改查 接口
  - Spring MVC 高级特性
    - 拦截服务接口来提供一些通用的功能（例如记日志）。
    - 文件的上传下载。
    - HTTP 请求的异步处理（通过多线程来提高服务的性能）。
  - RESTful 服务开发常用工具
    - Swagger : 自动生成接口服务文档。
    - WireMock : 快速伪造 RESTful 服务（使得前端可以和后台并行开发）。

- 第4章 使用 Spring Security 开发基于表单的登录

  - 介绍 Spring Security 的基本原理和核心概念。

  - 基于 Spring Security 的默认实现 开发 「 用户名 + 密码 」的认证 。
    （体验 开箱即用 的快速开发）
  - 扩展 Spring Security 的默认实现 来满足 个性化的需求。
    - 深入了解 Spring Security 的源码实现。
    - 实现自定义的登录方式 （开发 「 手机号 + 短信 」的认证 ） 。

- 第5章 使用 Spring Social 开发第三方登录
  - 介绍 OAuth 协议 和 Spring Social 的基本原理和核心概念。
  - 深入了解 Spring Social 的底层源码实现，以及扩展这些实现来适应不同的服务提供商。
    - 实现 QQ 认证 和 微信认证。
  - 介绍 Spring Security 中与 session 管理相关的特性。
    - 如超时处理，并发控制，集群环境等。

- 第6章 Spring Security OAuth 开发 APP 认证框架
  - Spring Security OAuth 简介。
  - 基于 Spring Security OAuth 搭建自己的认证服务器和资源服务器。
    - 重构原有认证功能，使其支持基于 Token 的认证方式 （实现 APP 登录的支持）。
  - 基于 OAuth 协议实现 SSO (单点登录)。
    - 如何控制 Token 的 生成 和 存储策略 以及 Token 的自动刷新。

- 第7章 使用 Spring Security 授权控制

  - Spring Security 中 与授权 相关的原理和概念。

  - 实现 3 种不同复杂度的授权机制。

    依据 授权逻辑 的复杂程度 将常见的 授权场景分类。

  - 重构代码。



