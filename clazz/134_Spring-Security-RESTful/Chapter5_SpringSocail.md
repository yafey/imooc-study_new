---
typora-root-url: ./
typora-copy-images-to: ./README_images/5
---



[返回](./README.md)

<details>
  <summary>点击时的区域标题</summary>
  <p> - 测试 测试测试</p>
  <p> 测试二 测试三 。。。。。 .</p>
  ```
    我是代码块1。我是代码块2。
  ```
</details>


# 第5章 使用 Spring Social 开发第三方登录

> 参考、整理自：
>
> - [九、SpringSocial基本原理 @Tinner丶](https://www.jianshu.com/p/9e3d2980d9de)
> - [SpringSecurity开发基于表单的认证(八) @ 云师兄 ](https://www.jianshu.com/p/9c8d197ccdab)



- 介绍 OAuth 协议 和 Spring Social 的基本原理和核心概念。
- 深入了解 Spring Social 的底层源码实现，以及扩展这些实现来适应不同的服务提供商。
  - 实现 QQ 认证 和 微信认证。
- 介绍 Spring Security 中与 session 管理相关的特性。
  - 如超时处理，并发控制，集群环境等。



## 5.1(5-1).  OAuth 协议 简介

不管是 Spring Social 还是 Spring Security OAuth，这两个框架都是根据 OAuth 协议来提供功能的。为了理解这两个框架的功能，我们需要对 OAuth 协议有一个较全面的理解。

> 1. OAuth 协议要解决的问题
> 2. OAuth 协议中的各种角色
> 3. OAuth 协议的运行流程

### 5.1.1. OAuth 协议 要解决的问题

首先我来举一个例子。

![image-20200226193523418](README_images/5/image-20200226193523418.png)

> 比如说我要开发一个微信美图助手，可以将用户在微信里面的自拍数据进行美化。这件事对于双方系统都是一件有利的事。对于微信来说，我提供这种服务，用户可能就更愿意把自拍放到微信上，而不是放到别的平台上去。而对于我自己的应用，默认的我可以把微信平台上的所有的这些用户都变为我的潜在用户，这比我从0开始一个个地去扩展用户的效率还是成本都好很多，这本身就是一种共赢的事情。

但是问题来了，微信肯定是不会允许我去随便地去获取 微信的 用户信息的。这时我就需要用户的一个授权，用户同意我去读 用户 在微信中的 自拍数据。有了这个授权之后我就可以告诉微信说用户同意了，这个时候微信就会把用户的 自拍数据 开放给我了。那么如何来得到用户的授权呢？我们可能会想到去要用户的微信名和微信密码，先不说安全问题，就算用户给了我微信名和密码，我拿着用户名和密码去微信中读取用户的 自拍数据，也是会存在很大的问题的。

> ​	问题1：***应用可以访问用户在微信上的所有数据***。因为如果用户把密码给我，我登录微信上之后我想看什么 看什么 ，比如说自己的通讯录好友，朋友圈等信息，并不只是 自拍数据。
> ​	问题2：***用户只有修改密码，才能收回授权***。因为我知道用户名密码了，但是用户并不能控制我到底能用多久，只要用户不更改密码我可以一直使用这个微信账号，但是用户一旦改了密码，其他的第三方应用也用不了了，而且下次再授权还是需要告诉我用户名密码。
> ​	问题3：***密码泄露的可能性大大提高***。如果用这种方式授权，就比如说我只是用户授权的其中之一的服务商，可能对于其他功能还会有很多授权，那么如果其中一个服务商出现了安全问题，将密码泄露了，那么可能用户的数据就泄露了，密码泄露的可能性就会大大提高。

那么为了解决这些问题，**OAuth 协议** 就诞生了，它出现的意义就在于，在用户给服务商授权的时候并不用给我用户名密码，而是可以交给我一个令牌（Token），我再去访问微信上 用户的 自拍数据 的时候 ， 我就不需要 用户的 用户名和密码了，而是用 用户的 令牌 去进行访问。有了这个机制，上述三个问题就会迎刃而解了。

> 首先， 在令牌上写着，你只能访问 用户的 自拍数据。你就没办法访问 用户 在微信上 其他的 数据 了。
>
> 第二， 令牌上会有一个有效期。比如 令牌上写着 这个令牌只能用一个月，那么 一个月 以后就再也不能访问 用户的 自拍数据 了。
>
> 最后，微信 根本就没有把密码给 我们的 美图助手 应用，而是给了它一个 令牌，所以也不会存在 用户密码泄露 的问题。

### 5.1.2. OAuth 协议 中的 各种角色

1. <span style="color:green">**服务提供商**（**Provider**）</span>

   **它用来提供 令牌**。因为之前说了 OAuth 协议 是基于 **令牌（Token）**的，这个令牌总不能让用户写个纸条给我，它本身就是一个电子化的东西。那么谁提供这个令牌，谁就是这个提供商，在我上边的例子中，主要是微信提供这个令牌，那么微信就是这个服务提供商。

   - <span style="color:green">(4) **认证服务器**（**Authenrization Serve**r）</span>

     它的作用是 **认证 用户的 身份 并且 产生 令牌**，<span style="color:green">令牌 是从这个角色中产生出来的。</span>

   - <span style="color:green">(5) **资源服务器**（**Resource Server**）</span>

     这个角色有两个作用：

     - 第一个作用就是 **保存用户的资源**，即 用户的 自拍数据 是保存在 资源服务器 上的。
     - 第二个作用就是 **验证令牌**。最终我们的 第三方应用 发送请求 的时候 是发送到 资源服务器的，发请求的时候 会带着 认证服务器 发给我们的 Token，由 资源服务器 去验证 这个 Token，如果验证过了 就会把 用户的 自拍数据 给第三方应用。

2. <span style="color:green">**资源所有者**（**Resource Owner**）</span>

   资源是指什么？资源就是 用户的 自拍数据。**这些数据所有者是用户，而不是微信，用户只是把这些资源放在了微信上。**那么这里的 资源所有者 就是 用户 。

3. <span style="color:green">**第三方应用**（**Client**）</span>

   第三方应用 也就是 我们例子中的 美图助手，它的目的就是 将 微信用户 转化为 第三方的用户，然后向微信提供服务。

> 认证服务器 和 资源服务器 在 角色上是 2 个，但是在物理上， 他们可以在 同一台 机子上。

![image-20200226201852804](README_images/5/image-20200226201852804.png)

### 5.1.3. OAuth 协议 的 运行流程

首先 用户 要访问 第三方应用。

1. 然后 第三方应用 就会 请求 用户 去授权。

   用户 必须要让 第三方应用 有权限 去访问 相应数据 才能 进行服务。

2. 如果 用户 同意授权，那么 第三方应用 就会去访问 服务提供商 的 认证服务器，告诉 认证服务器 用户同意 我访问 微信上的 自拍数据了，你给我一个令牌（申请令牌）。

3. 其次 认证服务器 会去验证 第三方应用 “说的是不是实话”，即 用户 是不是 真的 同意授权了。

4. 如果 验证 通过了， 认证服务器 就会给 第三方应用 发送一个令牌。

5. 第三方应用 拿到了这个令牌之后，它就可以使用 这个令牌 去 资源服务器 中申请 访问资源。

6. 资源服务器 会验证 （由 第三方应用 发出的）申请资源 请求 上 携带的 令牌，确认令牌无误之后，它会同意把 用户的 自拍数据 开放给 第三方应用。

在这几个步骤中，**第二步（用户同意授权）这个是关键**，因为有了这个授权之后，第三方应用 Client 才能去进行下面的一系列操作 （申请令牌、获取资源）。



![流程](README_images/5/image-20200226215552174.png)



### 5.1.4. OAuth 协议 中的 授权模式

在用户授权的时候，可以将授权模式分为以下几种：

> 1. 授权码模式（authorization code）
> 2. 密码模式（resource owner password credentials）
> 3. ~~客户端模式（client credentials）~~
> 4. ~~简化模式（implicit）~~

其中，用的最多的是 **授权码模式（authorization code）和密码模式（resource owner password credentials）**。

~~客户端模式（client credentials）和 简化模式（implicit）~~在实际开发应用过程中会用的比较少， 这里也不会涉及。



其中特别常见、特别主流、特别安全的一种授权模式，就是授权码模式（authorization code），下面我来详细介绍一下这种授权模式。

### 5.1.5. 授权码模式 简介

授权码模式 是四种授权模式中 <span style="color:green">**功能最完整、流程最严密**</span> 的一种授权模式，不管是微信也好、微博也好，几乎所有的互联网提供商都是采用这种方式来完成授权的。

##### 5.1.5.1. 授权码 模式 授权步骤

用户访问第三方客户端。

1. 如果第三方应用需要用户授权，它会将 用户 导向 认证服务器。
2. **用户 同意 授权 的这个动作 会在 `认证服务器` 上来完成**。
3. 如果 用户 同意 授权，那么 认证服务器 会将 用户 重新引导到 第三方应用上去。
   - 那么导回去 的时候 会导入到 第三方应用 的哪里呢？一般会指向到一个 URL 上去，这个 URL 是 第三方应用 Client 和 服务提供商 事先商量好的。
   - 当 认证服务器 将 用户 导入 第三方应用 的 URL 上去时，会携带一个**授权码**（这个授权码 并不是 第三方应用 需要的 那个令牌（Token））。
4. 第三方应用 在 收到 这个 授权码 之后，会拿着这个 **授权码** 向 认证服务器 上 **申请令牌**，（注意：这一步是在 第三方应用 Client 的 服务器后台 去完成的，对用户是不可见的）。
5. 然后 认证服务器 会核对 第三步给的 那个**授权码 **是不是 之前 给 第三方应用 Client 发过去的，如果确认无误，那么会向 第三方应用 Client **发送令牌（Token）**。

![image-20200226234340073](README_images/5/image-20200226234340073.png)

##### 5.1.5.2. 授权码模式特点

> 在整个过程中，我们了解到，就是因为在授权的时候会产生一个授权码，因此这个方式就叫做授权码模式。

这种模式主要有两个特点：(与其他 3 种 区分开)

1.  <span style="color:green">**用户 同意 授权 的动作是在 认证服务器 上去完成的。**</span>

   - 相比其它三种授权模式中， <span style="color:red">**密码模式和客户端模式，同意授权的动作是在第三方应用上去完成的。**</span>授权之后，第三方应用 Client 去向 认证服务器 申请令牌 的时候 带着一些信息（用户同意 第三方应用 Client 授权）。但是 认证服务器 是没办法确定 用户 是不是真的授权了，有可能这个授权信息是 第三方应用 Client 自己伪造的。
   -  <span style="color:green">**而在 授权码 模式中，“同意授权” 这个动作是在 认证服务器 上去完成的**</span>，所以 认证服务器 可以明确的 知道 用户 确实同意了 授权。

2.  <span style="color:green">**Client 换取令牌 的方式**。</span>

   第三方应用 Client 在整个申请 令牌（Token）的过程中向认证服务器进行了两个请求，第一个是拿到 授权码，第二次请求是拿着 授权码 去换取 真正的 令牌（Token）。

   - 也就是说在这个模式下 要求 第三方应用 必须要有一个服务器，并不是直接返回到 浏览器上，认证服务器 将 令牌 是发给 第三方应用 的 服务器的，安全性会更高。
   - 有些网站只有一些静态页面，没有服务器。这种情况下，它们可能会 用  简化模式（implicit），从 认证服务器 到 第三方 的时候，直接返回的 令牌。然后在第三方 页面上 用 脚本去读取。换句话说，简化模式（implicit）下，你从 浏览器 中就可以直接拿到令牌。

就是因为这两个特点，决定了 **授权码模式** 是 <span style="color:green">**功能最完整、流程最严密**</span> 的一种授权模式，也是现今 第三方应用服务商 最常用的 授权模式。



## 5.2(5-2). Spring Social 简介



> 前面 我们了解到 **OAuth 协议 其实是一个 授权协议**，其目的是 让用户 在不将 服务提供商 的用户名密码 提供给 第三方应用 的情况下，让 第三方应用 有权限访问 用户 存在 服务提供商 上的资源。



**那么 OAuth 协议 跟我们要实现的 第三方登陆 （QQ登录、微信登录等）是什么关系呢？**

*在前面的例子中，走完 授权协议，拿到令牌之后，说的场景是 拿用户的 自拍数据。* 

但是如果我们不去拿 用户的 自拍数据，而是拿 用户的 基本信息（比如 昵称、头像）。根据 用户 基本信息 构建 Authentivcation 并放入 SecurityContext 中去。

- 当我们往 SecurityContext 放入一个 经过验证的 Authentivcation 实例的时候，对于 Spring Security 来说，就是 登陆成功了。
- 如果我们 引导 用户 走完 下面的整个流程 ，实际上就是 拿 用户 在 服务提供商 上边的用户基本信息 登陆了 我们的 第三方应用。这个就是 第三方登录 实现的 基本原理和流程。

![第三方登录 的基本原理和流程](README_images/5/image-20200227112338834.png"第三方登录的 基本原理和流程")



### 5.2.1. Spring Socail 核心功能

> **Spring Social 的核心功能就是 封装 并实现了整个 授权流程。** 


那么 Spring Social 在这一过程中做了什么事情呢？

**Spring Social 就是将这一整个 流程 封装起来 并且实现了。**

- 它把 上面 整个流程 封装到了 `SocialAuthenticationFilter` 的过滤器中，然后把这个过滤器加入到了 Spring Security 的过滤器链上。当访问请求的时候，SocialAuthenticationFilter 会将请求拦截下来，然后将整个流程走完。实际上 就相当于 实现了 第三方登录。

![image-20200227113637177](README_images/5/image-20200227113637177.png)



### 5.2.2. Spring Social 接口说明

> Spring Social 是如何将上面的 流程 封装到 特定的接口和类中去的？以及这些接口和类的调用关系。

![5.2.2. Spring Social 接口说明](README_images/5/image-20200227234552236.png "5.2.2. Spring Social 接口说明")

#### 服务提供商相关

在整个流程上面，从第一步到第六步，都是需要跟 服务提供商 打交道的。

所以它的 **第一个接口叫--`ServiceProvider`**，它实际上就是 服务提供商 的一个抽象。

针对每一个 服务提供商（QQ、微信），都需要一个 ServiceProvider 接口的一个实现。Spring Social 提供了一个**`AbstractOauth2ServiceProvider`** 抽象类，实现了一些共有的东西，如果要自己写，只需要 继承 这个类 实现其中的 方法即可。

> 国内因为用 OAuth 协议比较晚，基本是用的都是 OAuth2 协议。

回过头来再看整个流程：

- ①第一步到第五步发放令牌其实是一个标准的流程。
- ②到了第六步（获取用户信息）的时候其实就是一个 个性化的实现，因为每一个 服务提供商 返回的用户信息的 数据结构 定义 都不一样。

针对①和②，SpringSocial 提供了两个接口：

- `Oauth2Operation`（封装第一步到第五步）。

  Spring 提供了一个默认的实现叫`Oauth2Template` ， 这个类会帮助我们去完成 Oauth 协议 的执行流程。

- `API`（个性化第六步），实际上没有一个明确的接口，因为每一个 服务提供商 对于用户基本信息的调用 都是有区别的。这里我们需要自己写一个接口来封装 第六步 获取用户信息 的行为。

  Spring Social 其实也提供了一个抽象类叫`AbstractOauth2ApiBinding` 帮助我们快速开发 第六步 的实现。

#### 第三方应用内部

到了第七步 实际上就跟 服务提供商 没有任何关系了。都是在 第三方应用Client 内部 去完成的。

- 第一个接口是 `Connection`，Spring Social 提供的实现类叫 `OAuth2Connection`。**其作用是 封装 前六步执行完毕之后 获取到的 用户信息**。

- `Connection` 是由 `ConnectionFactory` 工厂类 创建出来的，用到的类叫`OAuth2ConnectionFactory`。

  - **这个工厂类的 作用就是 为了创建 `Connection` 对象 来 获取用户信息。**

    但是用户信息是在`ServiceProvider` 中去构建的。**所以在 `OAuth2ConnectionFactory` 中肯定有一个`ServiceProvider` 实例**，即 `ServiceProvider` 被封装起来放到 `ConnectionFactory` 中去，以便去走 第一步到第五步 的流程 。

  - **注意：`Connection` 的对象名和字段名都是 固定的。**



之前说过，每一个 服务提供商 对于 第六步 获取到的 用户信息的 数据结构 都是不一样的，那么**`ConnectionFactory` 是如何做到将 这些 不同 数据结构的 信息 转化成 `Connection` 这样一个标准的数据结构（ 对象名和字段名 都是固定的 对象）的呢？**

- `ConnectionFactory` 有一个 `ApiAdapter` 。 将 不同格式的 用户信息 转化为 固定格式的 `Connection` 对象就是由 **`ApiAdapter` (Api 接口的 实现类)** 来完成。

  - `ApiAdapter` ，顾名思义，就是在 `Api` 和 `Connection` 之间进行一个 适配。

    转化成功之后就将向 `Connection` 中封装进去一个用户信息。



一般情况下，我们 业务系统 的用户 会存到 数据库 的 Table 里面（如 User 表）。

**如果 将 业务系统中的 用户信息 和 服务提供商 的 用户信息 关联到一块呢？**

- 换句话说， 服务提供商 里的 A 用户 登陆，如何知道是 业务系统的 张三 登陆呢？

在 Spring Social 中， **这个对应关系 是存在 数据库中的**。数据库中有一张 **UserConnection 表**，里面有 **业务系统的 userId 以及 服务提供商 的 Connection 之间的 对应关系**。

那么由谁来 操纵 这个 UserConnection  表呢？就是由 **UsersConnectionRepository**存储器 去实现的。在代码中用到的 实现类 叫做 **JdbcUsersConnectionRepository**, 这个类的作用就是 去数据库中 针对 用户 对应关系表 去做一些增删改查的操作。



## 5.3(5-3,5-4). 开发 QQ 登陆

> 参考 整理自： https://www.jianshu.com/p/6220b2745b24

![5.2.2. Spring Social 接口说明](README_images/5/image-20200227234552236.png "5.2.2. Spring Social 接口说明")

- 关键是要做一个 OAuth 流程， 拿到 服务提供商 QQ 的 用户信息，它是封装在 Connection 中的。
- 要创建 Connection， 需要一个 ConnectionFactory（OAuth2ConnectionFactory）这么一个工厂。
- 要构建 ConnectionFactory， 需要 2 个东西：ServiceProvider（服务提供商） 和 ApiAdapter （API 适配器）。 
- 要构建 ServiceProvider ， 也需要 2 个东西：OAuth2Operations （Spring Social 提供了一个 OAuth2Template 的默认实现） 和 Api（用来获取用户信息，它是跟 服务提供商 紧密相关的，每个 服务提供商 都不一样。我们从 Api 作为切入点 入手 进行实现）。



### 5.3.1. 整个开发流程如下：

1. 先写 Api 的实现。(获取用户信息 的 实现。)
2. 用默认的 OAuth2Template 作为 OAuth2Operations 的一个实现。
3. 然后用 上面的 2 个 东西（Api 和 OAuth2Operations ） 构建出 一个 ServiceProvider 。
4. 然后去实现 ApiAdapter。
5. 有了 ServiceProvider  和 ApiAdapter ， 就可以构建 ConnectionFactory 。
6. 然后就可以拿到 服务提供商 的 用户信息（Connection）了。
7. 在 数据库中创建 UserConnection  表，然后配置下 告诉 JdbcUsersConnectionRepository 数据库的信息 就可以了。



-----

因为这个可以在 browser 和 app 都可以使用，所以也写在 core 里面。

#### 5.3.1.1. Api 开发

1. Api开发 -- 声明获取用户信息接口

   Api 主要是为了 获取用户信息，因此我们只需要 声明一个 获取用户信息的 接口。

   ```java
   package com.yafey.security.core.social.qq.api;
   public interface QQ {
   	// Api 主要是为了 获取用户信息，因此我们只需要 声明一个 获取用户信息的 接口。
       QQUserInfo getUserInfo();
   }
   ```

2. 开发接口实现类

   接口实现类继承了 Spring Social 中的 AbstractOAuth2ApiBinding 并实现了 QQ 接口。

   > 我们 先看一下 Spring Social 中 AbstractOAuth2ApiBinding 中的部分源码：
   >
   > 这个抽象类里面声明了一个 accessToken 和一个 restTemplate。
   >
   > - accessToken 说明： 我们现在写的 Api 实现类（QQImpl） 在 整个流程里面是执行 第六步 获取用户信息的。要执行这一步，必须携带 第五步 拿到的 令牌。 所以 这个 accessToken  属性 保存的是 就是这个 令牌。
   >   - *授权登录中，获取每一个QQ用户的 openId 和登录信息都需要一个 token，而且每个人的 token 不可能都一样，因此我们的 accessToken  不能够是单例的。*
   > - restTemplate 主要用于帮助我们往 服务提供商 发 Restful/HTTP 请求的。
   >
   > ```java
   > package org.springframework.social.oauth2;
   > public abstract class AbstractOAuth2ApiBinding implements ApiBinding, InitializingBean {
   > 
   >     private final String accessToken;
   >     private RestTemplate restTemplate;
   > 
   >     protected AbstractOAuth2ApiBinding() {
   >         accessToken = null;
   >         restTemplate = createRestTemplateWithCulledMessageConverters();
   >         configureRestTemplate(restTemplate);
   >     }
   >         //省略几万字~~~
   > ```
   
   查看 QQ互联 接口 API，accessToken 已经在 AbstractOAuth2ApiBinding 中帮我们定义好了， 我们只需要 在 QQImpl 中定义 appID（appId 是我们接入 QQ 时分配的 ID） 和 openid（存储的是 获取到的用户 openId，唯一标识符） 即可。外加两个 URL 模板字符串 方便调用（一个是 get_user_info 获取用户信息的路径，另一个是 通过 access Token 去发请求 获取 openid 的路径）。

   - 构造函数中，我们调用了父类的构造方法，最主要的是第二个参数`TokenStrategy.ACCESS_TOKEN_PARAMETER`，表示我们去获取 openID 的时候，**token 是放在`请求参数`中的，父类默认是放在`请求头`里面的，所以我们这里必须这么写一下。**
   - getUserInfo 方法使用 openId 和 appId，发起 rest 请求从QQ那里获取到用户的具体信息，然后封装成 QQUserInfo 对象。
   
   ![QQ互联 接口 API](README_images/5/image-20200228164848393.png "QQ互联 接口 API")
   
   ```java
   package com.yafey.security.core.social.qq.api;
   // 每个人的 token 都是不一样的，因此我们的QQImpl不能够是单例的。即 不能使用 @Component 注解。
   public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {
   	// 通过 access Token 去发请求 获取 openid 的路径
   	private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";
   	// 获取用户信息的路径
   	private static final String URL_GET_USERINFO = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";
   	
   	private String appId;
   	
   	private String openId;
   	
   	private ObjectMapper objectMapper = new ObjectMapper();
   	
   	public QQImpl(String accessToken, String appId) {
           // token 是放在`请求参数`中的，父类默认是放在`请求头`里面的，所以我们这里必须这么写一下。
   		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
   		
   		this.appId = appId;
   		
   		String url = String.format(URL_GET_OPENID, accessToken);
   		String result = getRestTemplate().getForObject(url, String.class);
   		
   		System.out.println(result);
   		
   		this.openId = StringUtils.substringBetween(result, "\"openid\":\"", "\"}");
   	}
   	
   	/** 
   	 * 这个方法使用 openId 和 appId，发起 rest 请求从QQ那里获取到用户的具体信息，然后封装成 QQUserInfo 对象。
   	 */
   	@Override
   	public QQUserInfo getUserInfo() {
   		
   		String url = String.format(URL_GET_USERINFO, appId, openId);
   		String result = getRestTemplate().getForObject(url, String.class);
   		
   		System.out.println(result);
   		
   		QQUserInfo userInfo = null;
   		try {
   			userInfo = objectMapper.readValue(result, QQUserInfo.class);
   			userInfo.setOpenId(openId);
   			return userInfo;
			} catch (Exception e) {
   		throw new RuntimeException("获取用户信息失败", e);
   		}
   	}
   }
   ```
   
3. QQUserInfo , 这个就是把从QQ接收到的用户信息字段映射到的一个类中。

   OK，Api模块我们就开发完成了。

   ![image-20200228193505485](README_images/5/image-20200228193505485.png)



#### 5.3.1.2. OAuthOperations 开发

为什么我们不直接使用 OAuth2Template 呢，而要自己去实现一个 QQOAuth2Template？

这是因为 QQ 返回回来的信息中是 `text/html` 格式的，OAuth2Template 没有支持处理这种类型的请求，所以我们必须要自己手动定义一个 。

- createRestTemplate 方法添加了一个 StringHttpMessageConverter，这样我们就可以成功的将服务提供商返回的信息转换成对应的对象了。
- 那为什么我们还要重写 postForAccessGrant 方法呢？我们会在后面讲解。  



OAuthOperations 开发就算开发完成了，那么我们相当于凑齐了 ServiceProvider 的两大组件了，我们可以做 ServiceProvider 的实现了。



#### 5.3.1.3. ServiceProvider 开发

ServiceProvider 抛开 API 组件，主要是完成了OAuth中的步骤1-5。

`QQServiceProvider` 需要继承抽象类 `AbstractOAuth2ServiceProvider<QQ>` ，里面 的 泛型是 Api 接口的泛型，即我们自定义的 QQ 接口。

- getApi 方法： ServiceProvider 用来获取 用户信息 需要的方法。我们刚才说了每个用户的token 是不一样的，而且 token 是有期限的。所以我们不能够直接把 QQImpl 声明为单例的，这里必须要 new 一个出来。accessToken 保存在 父类中。

  

- 构造方法： 调用了父类的构造方法，并且参数是我们自己声明的 QQOAuth2Template。

  - 其中 appId 和 appSecret 是QQ分配给我们的。因为每个调用我们模块的这两个参数都不一样，所以需要传进来。
  - URL_AUTHORIZE 表示我们引导用户跳转的授权页面，对应步骤1。
  - URL_ACCESS_TOKEN 对应步骤4，表示去获取 token。

```java
package com.yafey.security.core.social.qq.connect;

public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {

	private String appId; // 整个应用 对于 QQ 来说，有一个固定的 appID。
	
	private static final String URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";
	
	private static final String URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";
	
	public QQServiceProvider(String appId, String appSecret) {
		// 其中 appId 和 appSecret 是QQ分配给我们的。因为每个调用我们模块的这两个参数都不一样，所以需要传进来。
		// URL_AUTHORIZE 表示我们引导用户跳转的授权页面，对应步骤1。
		// URL_ACCESS_TOKEN 对应步骤4，表示去获取 token。
		super(new QQOAuth2Template(appId, appSecret, URL_AUTHORIZE, URL_ACCESS_TOKEN));
		this.appId = appId;
	}
	
	@Override
	public QQ getApi(String accessToken) {
		return new QQImpl(accessToken, appId);
	}
}
```



#### 5.3.1.4. ApiAdapter

这个主要是用于适配作用的，试想，我们从QQ、微信、微博中获取到的 UserInfo 信息是五花八门的，但是我们的 Connection 想要的信息就那么多，如何适配呢？很简单直接继承 SpringSocial提供的 ApiAdapter 接口。

> QQAdapter 就是在我们自己写的 QQImpl （Api）所获取到的 个性化的 服务提供商 的 用户数据 和 Spring Social 标准的 数据结构 进行 适配。

- test 方法：表示和 QQ 的连接是否 还通畅，我们这里直接返回 true。

- `setConnectionValues` 方法：**这里就是 真正 做适配 的地方。**

  这个方法里面要做的就是 把 QQUserInfo （从 Api 接口获取的数据） 转换成 ConnectionValues 需要的数据。

  

当我们完成了 适配 和 Serviceprovider 后，我们就可以开始构造 我们的 ConnectionFactory 了。

```
package com.yafey.security.core.social.qq.connect;

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
```



#### 5.3.1.5. OAuth2ConnectionFactory 及 Connection

OAuth2ConnectionFactory 的构建非常简单，继承 OAuth2ConnectionFactory ， 泛型是 Api 接口类型，然后 将我们构建出的 QQServiceProvider 和 QQAdapter 传给 父类的构造函数即可。

然后 Spring Social 就可以利用它创建 Connection 了。

- 我们不用自己处理 Connection ， Spring Social 会根据 OAuth2ConnectionFactory 和 前面的代码，自动帮我们创建出来。

```java
package com.yafey.security.core.social.qq.connect;

public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

	// providerId 表示 服务提供商 的唯一标识 ，放在配置中。
	// appId 和 appSecret 也从外面传进来，交给 QQServiceProvider。 
	/**
	 * 将我们构建出的 QQServiceProvider 和 QQAdapter 传给 父类的构造函数即可。
	 */
	public QQConnectionFactory(String providerId, String appId, String appSecret) {
		super(providerId, new QQServiceProvider(appId, appSecret), new QQAdapter());
	}
}
```



#### 5.3.1.6. 配置SocialConfig

要把数据保存到数据库，我们只需要 配置数据库信息交给 Spring Social `JdbcUsersConnectionRepository` 即可。

Spring Socail 会使用 `JdbcUsersConnectionRepository`（UsersConnectionRepository 的 默认实现类） 来将 Connection 存储到 DBUserConnection中去。

1. 成员变量解析

   DataSource  表示数据源，repository 会使用到

   connectionSignUp 注册配置,详情请见下面的 第9部分拓展部分

   SecurityProperties，存放QQ和微信的配置，其中的成员变量包含了 appId 和 appSecret

2. **`getUsersConnectionRepository`**

   这里我们使用了 SpringSocial 默认提供的 JdbcUsersConnectionRepository，第一个参数是dataSource，第二个参数 connectionFactoryLocator 可以帮我们选择对应的ConnectionFactory ，我们这里只写了一个QQ登录，只有一个QQ的 ConnectionFactory，但是下一篇文章中，我们写微信登录的时候，那么就会有多的 ConnectionFactory 了。`Encryptors.noOpText()` 表示我们对数据不加密，这里我们只是为了演示使用，**实际上我们的数据入库的时候，对于 access_token 这类敏感信息还是需要加密的**。

3. `addConnectionFactories`

  把我们的 QQFactory 和 WeixinFactory 加进去对于微信的实现我们下一篇文章讲，这里先卖个关子 。

4. `getUserIdSource`

   这个也是 SpringBoot2 里面最恶心的地方了，在 1.5 的版本中是没有这个东西了，这个方法主要是被 Filter 调用的，我们就返回 SpringSocial 默认提供的实现就可以了。

```java
package com.yafey.security.core.social;

@Configuration
@EnableSocial  // 声明 使用 Spring Social
public class SocialConfig extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator, Encryptors.noOpText());
		repository.setTablePrefix("yafey_");  // 假设我们的 表名 规范里面有统一的前缀。
		return repository;
	}

}
```

#### 5.3.1.7. 建立表

Spring-social-core 包提供了 SQL 语句，直接使用就可以了。（和 JdbcUsersConnectionRepository 放在同一个 package 下面）

userId 就是我们的业务系统 userId，providerId 表示 服务提供商 Id，providerUserId 就是 openId。这 3 个 字段是 这张表的 主键。

```sql
create table yafey_UserConnection (  -- 假设我们的 表名 规范里面有统一的前缀。
    userId varchar(255) not null,  -- 业务系统 userId
    providerId varchar(255) not null,  -- 服务提供商 Id
    providerUserId varchar(255),  -- 对于 QQ 来说 就是 openId 
    rank int not null,
    displayName varchar(255),
    profileUrl varchar(512),
    imageUrl varchar(512),
    accessToken varchar(512) not null,
    secret varchar(512),
    refreshToken varchar(512),
    expireTime bigint,
    primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);
```

#### 5.3.1.8. 修改 MyUserDetailsService 实现 SocialUserDetailsService 接口

在我们进行QQ 登录的时候，我们会根据 providerId 和 providerUserId 拿到 userId，然后通过 userId 拿到 用户的 具体业务信息。其实 SpringSocial 也提供了一个接口 SocialUserDetailsService ,帮助我们获取 user 的信息，所以我们也让 MyUserDetailsService 实现了 SocialUserDetailsService 接口， 并实现 loadUserByUserId 方法。

SocialUserDetails 是 UserDetails 的 子类，只不过多了一个 getUserId(); 方法。如果用 username 作为 唯一标识，这个方法等同于 getUsername 。

然后将 这个类 从 security-browser 挪到 browser-demo project 中。 ------ 因为 userId 这种 是 具体的 业务数据。

```java
package com.yafey.security.browser;

@Component
@Slf4j
public class MyUserDetailsService implements UserDetailsService,SocialUserDetailsService {

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
    ...其他代码...
}
```



#### 5.3.1.9. 新建 QQProperties

```java
package com.yafey.security.core.properties;

// 父类中声明了 appid 和 appSecret
@Data
public class QQProperties extends SocialProperties {
	
	private String providerId = "qq";  // 标识符 就设置为 qq 就可以
}
```

#### 5.3.1.10. 新建 SocialProperties

然后把 QQProperties 加进去，然后 把 SocialProperties 加到 SecurityProperties 中。

```java
package com.yafey.security.core.properties;
import lombok.Data;

@Data
public class SocialProperties {
	private QQProperties qq = new QQProperties();
}
```



```java
package com.yafey.security.core.properties;

@Data
@ConfigurationProperties(prefix = "yafey.security")
public class SecurityProperties {
	
	private BrowserProperties browser = new BrowserProperties();
	private ValidateCodeProperties code = new ValidateCodeProperties();

	private SocialProperties social = new SocialProperties();
}
```

#### 5.3.1.11. 新建 QQAutoConfig 配置类

继承 SocialAutoConfigurerAdapter， 实现 createConnectionFactory 方法。----- 将 配置的 appid 、appSecret 和 providerId 构建 ConnectionFactory 。 

```java
package com.yafey.security.core.social.qq.config;

@Configuration
@ConditionalOnProperty(prefix = "yafey.security.social.qq", name = "app-id") // 只有在系统中 配置 app-id,下面的配置才会生效。
public class QQAutoConfig extends SocialAutoConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;

	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		QQProperties qqConfig = securityProperties.getSocial().getQq();
		return new QQConnectionFactory(qqConfig.getProviderId(), qqConfig.getAppId(), qqConfig.getAppSecret());
	}
}
```

#### 5.3.1.12. 添加 social 配置 @ browser-demo

```yaml
yafey:
  security:
    social:
      qq:
        app-id: 
        app-secret: 
        providerId: qq
    ...其他配置...
```

#### 5.3.1.13. 配置 SocialConfigurer ，加到 过滤器链上。

我们把所有的组件都已经完成好了，接下来就是配置我们的过滤器了

在 SocialConfig 中我们先暂时直接 new SpringSocialConfigurer。

```java
package com.yafey.security.core.social;

@Configuration
@EnableSocial  // 声明 使用 Spring Social
public class SocialConfig extends SocialConfigurerAdapter {
	... 其他代码...
        
	@Bean
	public SpringSocialConfigurer yafeySocialSecurityConfig() {
		return new SpringSocialConfigurer();
	}
}
```

继承的 SpringSocialConfigurer 其实生成了一个`SocialAuthenticationFilter`，而这个Filter默认只会拦截 `/auth` 开头的请求路径。

```java
package com.yafey.security.browser;

@Configuration
public class BrowserSecurityConfig extends AbstractChannelSecurityConfig {
    
    @Autowired
	private SpringSocialConfigurer yafeySocialSecurityConfig;
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		applyPasswordAuthenticationConfig(http);
		
		http
			.apply(yafeySocialSecurityConfig) // 添加 social 的配置
		;
		... 其他代码 ... 
	}
    ... 其他代码 ... 
}			
```

#### 5.3.1.14. 在 self-login.html 新增链接

SocialAuthenticationFilter 默认会拦截 `/auth` 开头的链接，qq 是 ProviderID 

```html
<h3>社交登录</h3>
<!-- SocialAuthenticationFilter 默认会拦截 /auth 开头的链接，qq 是 ProviderID -->
<a href="/auth/qq">QQ登录</a>
```



**目前只是能跑起来。还有一些问题需要处理。**

![social-qq-1.gif](README_images/5/social-qq-1.gif)



## 5.4(5-5,5-6). 完善 QQ 登陆

上图中出现的 `param client_id is wrong or lost (100001)` ， 表示 回调地址 出错。

在 OAuth 流程的 第三步， 当 用户同意授权后，服务提供商 会跳回到 第三方应用 ， 就需要 这个 回调地址。这个地址是 我们在 QQ 互联 上申请应用的时候 需要填写的 ，示例如下：

![image-20200301133222456](README_images/5/image-20200301133222456.png)



### 5.4.1. 修改 redirectURL

让我们的回调地址指向到 上面 申请时候配置的 地址。

1. 修改 hosts , 让 www.pinzhi365.com 指向 localhost/127.0.0.1

   ```hosts
   127.0.0.1 www.pinzhi365.com
   ```

   

2. 修改 Social filter 默认拦截的 地址。

   新建 YafeySpringSocialConfigurer

   我们这里使用了 `filter.setFilterProcessesUrl(filterProcessesUrl);` 来改变默认的配置。为什么是在 setFilterProcessesUrl 配置呢？

   - 因为 SpringSocialConfigurer 在把 Filter 放到过滤器链之前会调用一个 `postProcess` 方法，所以我们在调用父类的 postProcess 之后，我们就在这里设置 processingUrl。除此之外，我们的请求跳转还涉及到 providerId，**这个 providerId 也是我们实现跳转和授权完成之后回调的关键**。我们自定义 Filter 拦截 `/qqLogin/callback.do` 的请求会跳转到QQ登录，因此 ConnectionProvider 的 providerId 也必须配置成 `callback.do`，filterProcessesUrl 为 qqLogin。

   ```java
   package com.yafey.security.core.social;
   
   public class YafeySpringSocialConfigurer extends SpringSocialConfigurer {
   	
   	private String filterProcessesUrl;
   	
   	public YafeySpringSocialConfigurer(String filterProcessesUrl) {
   		this.filterProcessesUrl = filterProcessesUrl;
   	}
   	
   	@SuppressWarnings("unchecked")
   	@Override
   	protected <T> T postProcess(T object) {
   		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
   		filter.setFilterProcessesUrl(filterProcessesUrl);
   		return (T) filter;
   	}
   }
   ```

3. 修改 SocialConfig

   ```java
   package com.yafey.security.core.social;
   
   @Configuration
   @EnableSocial  // 声明 使用 Spring Social
   public class SocialConfig extends SocialConfigurerAdapter {
   	...其他代码...
   	@Autowired
   	private SecurityProperties securityProperties;
   	
   	@Bean
   	public SpringSocialConfigurer yafeySocialSecurityConfig() {
   		String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
   		YafeySpringSocialConfigurer configurer = new YafeySpringSocialConfigurer(filterProcessesUrl);
   		return configurer;
   	}
   ```

4. 配置 ， 和 申请时的 uri 保持一致。

   ```yaml
   yafey:
     security:
       social:
         qq:
           app-id: 
           app-secret: 
           filterProcessUrl: /qqLogin
           providerId: callback.do
   ```

5. 修改 self-login.html 的链接

   ```html
   	<h3>社交登录</h3>
   	<!-- SocialAuthenticationFilter 默认会拦截 /auth 开头的链接，qq 是 ProviderID -->
       <a href="/qqLogin/callback.do">QQ登录</a>
   ```

   使用 www.pinzhi365.com/self-login.html , 因为修改了 hosts, 所以访问的还是 本地的 服务。

   > 查看日志， 发现会访问 signIn 地址， 因为这个地址没有 排除掉，所以跳到了需要授权的页面。
   >
   > ```
   > 引发跳转的请求是: http://www.pinzhi365.com/signin
   > ```

   

   ![social-qq-2.gif](README_images/5/social-qq-2.gif)

   

### 5.4.2. Spring Social 流程说明

Spring Social 和 用户名密码 或 手机号 登陆的 核心流程基本上是一致的。

1. 有一个拦截器（SocialAuthenticationFilter) 去拦截特定的请求 （默认是 `/auth/<providerId>` )。
2. 把这个请求中 需要做身份认证的信息 封装到一个 Authentication 的实现类 中。
3. 然后把 Authentication 实现类 交给 AuthenticationManager。
   - <span style="color:green;font-family:'PMingLiU,MingLiU,KaiTi_GB2312'">*在 Spring Social 中，在 封装 Authentication  给 AuthenticationManager 的时候，用到了一个接口--SocialAuthenticationService， 在这里 具体的实现类是 OAuth2AuthenticationService。它的作用是 执行整个 OAuth2 的 流程。*</span>
   - <span style="color:green;font-family:'PMingLiU,MingLiU,KaiTi_GB2312'">*然后在执行的过程中会去调用我们自己写的 ConnectionFactory，拿到 ServiceProvider ，ServiceProvider 里面有 一个 OAuth2Operators（OAuth2Template），可以帮助 Spring Social 完成 整个的流程。*</span>
   - <span style="color:green;font-family:'PMingLiU,MingLiU,KaiTi_GB2312'">*完成流程后， 会拿到 服务提供商 的用户信息，封装到 Connection 中。 Connection 会被封装成 一个 SocialAuthenticationToken （Authentication）， 然后将 SocialAuthenticationToken 交给 AuthenticationManager。*</span>
4. AuthenticationManager 根据 传进来的 Authentication 的类型， 从它管理的 AuthenticationProvider 接口实现类 中 挑一个 Provider 来处理 传进来的 校验信息。
   - <span style="color:green;font-family:'PMingLiU,MingLiU,KaiTi_GB2312'">*AuthenticationManager 会挑选 SocialAuthenticationProvider 来处理 这个 Token （SocialAuthenticationToken ）。在处理的 时候， 会根据 Connection （也就是 服务提供商 的 用户信息），使用 JdbcUsersConnectionRepository 这个类 到数据库中 查找到 业务系统的 UserId。*</span>
   - <span style="color:green;font-family:'PMingLiU,MingLiU,KaiTi_GB2312'">*查找到 UserId 后， 根据 UserId 去调 我们自己写的 SocialUserDetailService ，这个类里面，去业务系统里 把 真正要放到 session 中的 用户信息 查出来（也就是 SocialUserDetails）, 然后把 SocialUserDetails 中的用户信息 放到 最终的 SocialAuthenticationToken ，并标记为 已经过身份校验，然后 放到 SecurityContext 中，最终放到 session 中。*</span>
5. 在处理的过程中， 会去调用我们自己写的 UserDetailService 接口的实现 来获取业务系统中 用户的 信息。 然后把 业务系统的 用户信息 封装在 UserDetail 接口的实现类 中。
6. 然后进行一系列的 检查和 校验， 如果都通过了，然后把 业务系统的用户信息 封装在 Authentication 中，并把它 标记为 `已校验` 的， 放到 SecurityContext 中。



PS：

- 蓝色的这些 是 Spring Social 封装好的；
- 橙色的这些 是 我们自己写的。



![Spring Social 流程说明](README_images/5/image-20200301143024168.png "Spring Social 流程说明")



![5.2.2. Spring Social 接口说明](README_images/5/image-20200227234552236.png "5.2.2. Spring Social 接口说明")





### 5.4.3 (5-6). Spring Social 流程源码分析

我们来回想下刚才流程出错的场景：我们已经 跳到 QQ 上，手机扫码了，说明我们已经进行 授权的确认了，确认后，跳回到了我们的 第三方应用。但是它并没有像 我们想象的那样 --- 进到系统里面去，而是跳到了 signin 的 url 上去。所以我们出问题的点 还是在 走 OAuth 的流程中出现的问题（也就是说我们最终 还没有 获取到一个正确 SocialAuthenticationToken 交给 AuthenticationManager 去验证，而是在 OAuth2AuthenticationService 去走 OAuth 流程的过程中出现了问题）。



OAuth2AuthenticationService，它是 SocialAuthenticationFilter 的关键 service，在这里我们主要是简单讲讲它的原理。这个 service 中有一个 getAuthToken 方法。

因为第三步中 QQ 会返回授权码 code，如果发现 没有这个 code，SpringSocial 会认为是第一步，也就是要去授权认证，因此会作重定向。但是如果我们发现我们得到了 code，这个时候我们就会利用 code 去拿 token 信息。

我们在 L98 和 L103 分别打断点，看看发生了什么。

我们可以看到 这里抛出了一个 RestClientException 异常，异常信息为 ：`No avaiable HttpMessageConventer found for response [Map] and content type [text/html]` ，然后 跑到 unsuccessfulAuthentication ，即交给 Social 的 失败处理器 处理。



![image-20200301194736270](README_images/5/image-20200301194736270.png)



查看 SocialAuthenticationFilter ， 默认的 失败处理器 的处理是 重定向到 DEFAULT_FAILURE_URL （/signin）的 url 上。

```java
package org.springframework.social.security;
public class SocialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private SimpleUrlAuthenticationFailureHandler delegateAuthenticationFailureHandler;

	public SocialAuthenticationFilter(AuthenticationManager authManager, UserIdSource userIdSource, UsersConnectionRepository usersConnectionRepository, SocialAuthenticationServiceLocator authServiceLocator) {
		super(DEFAULT_FILTER_PROCESSES_URL);
		setAuthenticationManager(authManager);
		this.userIdSource = userIdSource;
		this.usersConnectionRepository = usersConnectionRepository;
		this.authServiceLocator = authServiceLocator;
		this.delegateAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler(DEFAULT_FAILURE_URL);
		super.setAuthenticationFailureHandler(new SocialAuthenticationFailureHandler(delegateAuthenticationFailureHandler));
	}
	
	private static final String DEFAULT_FAILURE_URL = "/signin";
	
	private static final String DEFAULT_FILTER_PROCESSES_URL = "/auth";
	
	... 其他代码 ...
}
```

默认的 OAuth2Template 中没有 处理 `text/html` 的 converter, 所以我们要 覆盖掉下面的方法，让它可以处理 `text/html`。

![image-20200301222046507](README_images/5/image-20200301222046507.png)

新建 QQOAuth2Template ，

```java
package com.yafey.security.core.social.qq.connect;

@Slf4j
public class QQOAuth2Template extends OAuth2Template {
	
	public QQOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
		// 默认为 false ， 只有设置为 true 的时候才会传 clientId 和 clientSecret。 
		// 详见 org.springframework.social.oauth2.OAuth2Template.authenticateClient(String)
		 setUseParametersForClientAuthentication(true); 
	}
	
	// 解析 QQ 的 response，详见 org.springframework.social.oauth2.OAuth2Template.postForAccessGrant(String, MultiValueMap<String, String>)
	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		 String responseStr = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
		 
		 log.info("获取accessToke的响应："+responseStr);
		 
		 String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(responseStr, "&");
		 
		 String accessToken = StringUtils.substringAfterLast(items[0], "=");
		 Long expiresIn = new Long(StringUtils.substringAfterLast(items[1], "="));
		 String refreshToken = StringUtils.substringAfterLast(items[2], "=");
		 
		 return new AccessGrant(accessToken, null, refreshToken, expiresIn);
	}
	
	// 增加 处理 content type 为 “text/html” 格式的 MessageConverter 
	@Override
	protected RestTemplate createRestTemplate() {
		 RestTemplate restTemplate = super.createRestTemplate();
		 restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		 return restTemplate;
	}

}
```

然后 在 QQServiceProvider 中， 使用 QQOAuth2Template 替换 默认的 OAuth2Template 。

在我们拿到 Token 之后，Filter 会拿着这个 Token 选择一个 Provider 做验证，Provider 会使用 Repository **到数据库中获取 userId，如果获取不到，就会跳转到 signUp 页面**，提示用户去注册。而且在跳转之前我们可以看到，session中已经存放了connection的信息，具体步骤如下：

- 第1步：根据得到的 token 去验证
- 第2步：provider 中通过 repostitory 去拿对应的 userId
- 第3步：跳转到注册页面

![第1步：根据得到的token去验证](README_images/5/image-20200301231010066.png "第1步：根据得到的token去验证")

![第2步：provider 中通过 repostitory 去拿对应的 userId](README_images/5/image-20200301231023279.png "第2步：provider 中通过 repostitory 去拿对应的 userId")

![第3步：跳转到注册页面](README_images/5/image-20200301231031809.png "第3步：跳转到注册页面")



基于此，我们还需要设置一个注册页面。



### 5.4.4. 注册页面

1. 标准注册页面 yafey-signUp.html

   ```html
   <!DOCTYPE html>
   <html>
   <head>
   <meta charset="UTF-8">
   <title>标准注册页面</title>
   </head>
   <body>
   	<h2>标准注册页面</h2>
   	<h3>这是系统注册页面，请配置  yafey.security.browser.signUpUrl 属性来设置自己的注册页</h3>
   </body>
   </html>
   ```

   

2. 在 BrowserProperties 新增 signUpUrl 属性。

3. 配置 demo.yaml

   ```yaml
   yafey:
     browser:
       signUpUrl: /demo-signUp.html
   ```

4. 新建 demo-signUp.html

   ```html
   <!DOCTYPE html>
   <html>
   <head>
   <meta charset="UTF-8">
   <title>Demo注册页</title>
   </head>
   <body>
       <h2>Demo注册页</h2>
       
       <form action="/user/regist" method="post">
           <table>
               <tr>
                   <td>用户名:</td> 
                   <td><input type="text" name="username"></td>
               </tr>
               <tr>
                   <td>密码:</td>
                   <td><input type="password" name="password"></td>
               </tr>
               <tr>
                   <td colspan="2">
                       <button type="submit" name="type" value="regist">注册</button>
                       <button type="submit" name="type" value="binding">绑定</button>
                   </td>
               </tr>
           </table>
       </form>
   </body>
   </html>
   ```

   

5. 注册逻辑

   **`ProviderSignInUtils` 是SpringSocial提供的一个工具类，帮助我们把用户信息绑定到对应的账户中去。**

   ```java
   @RestController
   public class UserController {
       
       @Autowired
       private ProviderSignInUtils providerSignInUtils;
       
       @PostMapping("/user/regist")
       public void regist(User user, HttpServletRequest request) {
           
           //不管是注册用户还是绑定用户，都会拿到一个用户唯一标识。
           String userId = user.getUsername();
           providerSignInUtils.doPostSignUp(userId, new ServletWebRequest(request));
       }
       ... 其他代码 ...
   }
   ```

6. 在 SocialConfig 中 注册 providerSignInUtils

   ```java
   	@Bean
   	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
   		return new ProviderSignInUtils(connectionFactoryLocator,
   				getUsersConnectionRepository(connectionFactoryLocator)) {
   		};
   	}
   ```

7. 在 BrowserSecurityConfig 中，将 signUpUrl 和 "/user/regist" url 设置为不需要校验。

   ```
   ,securityProperties.getBrowser().getSignUpUrl()
   ,"/user/regist"
   ```



### 5.4.5. 从 session中获取 Connection 信息

在 重定向到 注册页面 之前，Spring Social 会将 Connection 放到 session 中， 因此，在注册页面我们可以从 session 中 取出 用户 在 服务提供商 的信息。相关操作可以使用 providerSignInUtils 类。

![image-20200302001417579](README_images/5/image-20200302001417579.png)



### 5.4.6. 新用户直接登录

有时候我们发现在用第三方授权登录后，不需要用户在注册信息，那这个是怎么实现的呢？我们先看看 SocialAuthenticationProvider 源码

![image-20200302001947761](README_images/5/image-20200302001947761.png)

在toUserId中我们刚才找不到用户的时候直接跳转到了注册页面，原因是Repostiry在寻找userId的时候的逻辑是这样的：

如果找不到用户且connectionSignUp为空了，就会返回一个空的List，但是如果我们实现一个connectionSignUp，这里就可以根据我们的根据我们的逻辑把这个userId放到数据库中去，所以我们这里要自己实现一个connectionSignUp。

这样配置后，我们即使在数据库中没有该用户也能够拿到用户信息 。

![image-20200302002023313](README_images/5/image-20200302002023313.png)

```
@Component
public class DemoConnectionSignUp implements ConnectionSignUp {

    /* (non-Javadoc)
     * @see org.springframework.social.connect.ConnectionSignUp#execute(org.springframework.social.connect.Connection)
     */
    @Override
    public String execute(Connection<?> connection) {
        //根据社交用户信息默认创建用户并返回用户唯一标识
        return connection.getDisplayName();
    }

}
```



## 5.5.(5-7) 开发微信登陆

SpringSocial 开发微信登录。大部分其实和 QQ 登录很相似，原理就不赘述了。



### 5.5.1. Properties

1. WeixinProperties

   ```java
   package com.yafey.security.core.properties;
   
   @Data
   public class WeixinProperties extends SocialProperties {
   	
   	/**
   	 * 第三方id，用来决定发起第三方登录的url，默认是 weixin。
   	 */
   	private String providerId = "weixin";
   
   }
   ```

2. SocialProperties

   ```
   package com.yafey.security.core.properties;
   
   import lombok.Data;
   
   @Data
   public class SocialProperties {
   	private String filterProcessesUrl = "/auth";
   
   	private QQProperties qq = new QQProperties();
   	
   	private WeixinProperties weixin = new WeixinProperties();
   }
   ```

3. yaml

   ```yaml
   yafey:
     browser:
       signUpUrl: /demo-signUp.html
     security:
       code:
         image:
           length: 6
           width: 100
           url: /users,/user/*
       social:
         filterProcessUrl: /qqLogin
         qq:
           app-id: 
           app-secret: 
           providerId: callback.do
         weixin:
           app-id: wxd99431bbff8305a0
           app-secret: 60f78681d063590a469f1b297feff3c4
   ```





### 5.5.2. Api 开发 

1. 声明获取用户信息接口

   ```java
   package com.yafey.security.core.social.weixin.api;
   /**
    * 微信API调用接口
    */
   public interface Weixin {
   
   	WeixinUserInfo getUserInfo(String openId);
   	
   }
   ```

   

2. WeixinUserInfo

   ```java
   package com.yafey.security.core.social.weixin.api;
   
   import lombok.Data;
   
   /**
    * 微信用户信息
    */
   @Data
   public class WeixinUserInfo {
   	
   	/**
   	 * 普通用户的标识，对当前开发者帐号唯一
   	 */
   	private String openid;	
   	/**
   	 * 普通用户昵称
   	 */
   	private String nickname;
   	/**
   	 * 语言
   	 */
   	private String language;
   	/**
   	 * 普通用户性别，1为男性，2为女性
   	 */
   	private String sex;
   	/**
   	 * 普通用户个人资料填写的省份
   	 */
   	private String province;
   	/**
   	 * 普通用户个人资料填写的城市
   	 */
   	private String city;
   	/**
   	 * 国家，如中国为CN
   	 */
   	private String country;
   	/**
   	 * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
   	 */
   	private String headimgurl;
   	/**
   	 * 用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
   	 */
   	private String[] privilege;
   	/**
   	 * 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
   	 */
   	private String unionid;
   	
   }
   ```

   

3. 获取用户信息接口实现类

   > 和QQ区别最大的是，我们没有在去获取openId了，而且我们的getUserInfo里面多了一个 openId 参数，**为啥不需要去获取openId了呢？是因为微信通过授权码拿到token后同时也把openId带回来了，所以帮我们省去了步骤 。**

   ```java
   package com.yafey.security.core.social.weixin.api;
   
   /**
    * Weixin API调用模板， scope为Request的Spring bean, 根据当前用户的accessToken创建。
    */
   public class WeixinImpl extends AbstractOAuth2ApiBinding implements Weixin {
   	
   	/**
   	 * 
   	 */
   	private ObjectMapper objectMapper = new ObjectMapper();
   	/**
   	 * 获取用户信息的url
   	 */
   	private static final String URL_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?openid=";
   	
   	/**
   	 * @param accessToken
   	 */
   	public WeixinImpl(String accessToken) {
   		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
   	}
   	
   	/**
   	 * 默认注册的StringHttpMessageConverter字符集为ISO-8859-1，而微信返回的是UTF-8的，所以覆盖了原来的方法。
   	 */
   	protected List<HttpMessageConverter<?>> getMessageConverters() {
   		List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
   		messageConverters.remove(0);
   		messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
   		return messageConverters;
   	}
   
   	/**
   	 * 获取微信用户信息。
   	 */
   	@Override
   	public WeixinUserInfo getUserInfo(String openId) {
   		String url = URL_GET_USER_INFO + openId;
   		String response = getRestTemplate().getForObject(url, String.class);
   		if(StringUtils.contains(response, "errcode")) {
   			return null;
   		}
   		WeixinUserInfo profile = null;
   		try {
   			profile = objectMapper.readValue(response, WeixinUserInfo.class);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		return profile;
   	}
   
   }
   ```

   

OK，API部分开发完成。



### 5.5.3 OAuthOperations开发

1. WeixinOAuth2Template

   ```java
   package com.yafey.security.core.social.weixin.connect;
   
   /**
    * 完成微信的OAuth2认证流程的模板类。国内厂商实现的OAuth2每个都不同, spring默认提供的OAuth2Template适应不了，只能针对每个厂商自己微调。
    *
    */
   public class WeixinOAuth2Template extends OAuth2Template {
   	
   	private String clientId;
   	
   	private String clientSecret;
   
   	private String accessTokenUrl;
   	
   	private static final String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
   	
   	private Logger logger = LoggerFactory.getLogger(getClass());
   
   	public WeixinOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
   		super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
   		setUseParametersForClientAuthentication(true);
   		this.clientId = clientId;
   		this.clientSecret = clientSecret;
   		this.accessTokenUrl = accessTokenUrl;
   	}
   	
   	/* (non-Javadoc)
   	 * @see org.springframework.social.oauth2.OAuth2Template#exchangeForAccess(java.lang.String, java.lang.String, org.springframework.util.MultiValueMap)
   	 */
   	@Override
   	public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri,
   			MultiValueMap<String, String> parameters) {
   		
   		StringBuilder accessTokenRequestUrl = new StringBuilder(accessTokenUrl);
   		
   		accessTokenRequestUrl.append("?appid="+clientId);
   		accessTokenRequestUrl.append("&secret="+clientSecret);
   		accessTokenRequestUrl.append("&code="+authorizationCode);
   		accessTokenRequestUrl.append("&grant_type=authorization_code");
   		accessTokenRequestUrl.append("&redirect_uri="+redirectUri);
   		
   		return getAccessToken(accessTokenRequestUrl);
   	}
   	
   	public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
   		
   		StringBuilder refreshTokenUrl = new StringBuilder(REFRESH_TOKEN_URL);
   		
   		refreshTokenUrl.append("?appid="+clientId);
   		refreshTokenUrl.append("&grant_type=refresh_token");
   		refreshTokenUrl.append("&refresh_token="+refreshToken);
   		
   		return getAccessToken(refreshTokenUrl);
   	}
   
   	@SuppressWarnings("unchecked")
   	private AccessGrant getAccessToken(StringBuilder accessTokenRequestUrl) {
   		
   		logger.info("获取access_token, 请求URL: "+accessTokenRequestUrl.toString());
   		
   		String response = getRestTemplate().getForObject(accessTokenRequestUrl.toString(), String.class);
   		
   		logger.info("获取access_token, 响应内容: "+response);
   		
   		Map<String, Object> result = null;
   		try {
   			result = new ObjectMapper().readValue(response, Map.class);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		
   		//返回错误码时直接返回空
   		if(StringUtils.isNotBlank(MapUtils.getString(result, "errcode"))){
   			String errcode = MapUtils.getString(result, "errcode");
   			String errmsg = MapUtils.getString(result, "errmsg");
   			throw new RuntimeException("获取access token失败, errcode:"+errcode+", errmsg:"+errmsg);
   		}
   		
   		WeixinAccessGrant accessToken = new WeixinAccessGrant(
   				MapUtils.getString(result, "access_token"), 
   				MapUtils.getString(result, "scope"), 
   				MapUtils.getString(result, "refresh_token"), 
   				MapUtils.getLong(result, "expires_in"));
   		
   		accessToken.setOpenId(MapUtils.getString(result, "openid"));
   		
   		return accessToken;
   	}
   	
   	/**
   	 * 构建获取授权码的请求。也就是引导用户跳转到微信的地址。
   	 */
   	public String buildAuthenticateUrl(OAuth2Parameters parameters) {
   		String url = super.buildAuthenticateUrl(parameters);
   		url = url + "&appid="+clientId+"&scope=snsapi_login";
   		return url;
   	}
   	
   	public String buildAuthorizeUrl(OAuth2Parameters parameters) {
   		return buildAuthenticateUrl(parameters);
   	}
   	
   	/**
   	 * 微信返回的contentType是html/text，添加相应的HttpMessageConverter来处理。
   	 */
   	protected RestTemplate createRestTemplate() {
   		RestTemplate restTemplate = super.createRestTemplate();
   		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
   		return restTemplate;
   	}
   
   }
   ```

2. WeixinAccessGrant

   ```java
   package com.yafey.security.core.social.weixin.connect;
   
   /**
    * 微信的access_token信息。与标准OAuth2协议不同，微信在获取access_token时会同时返回openId,并没有单独的通过accessToke换取openId的服务
    * 
    * 所以在这里继承了标准AccessGrant，添加了openId字段，作为对微信access_token信息的封装。
    *
    */
   @Data
   public class WeixinAccessGrant extends AccessGrant {
   	
   	private static final long serialVersionUID = -7243374526633186782L;
   	
   	private String openId;
   	
   	public WeixinAccessGrant() {
   		super("");
   	}
   
   	public WeixinAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn) {
   		super(accessToken, scope, refreshToken, expiresIn);
   	}
   
   }
   ```

3. ServiceProvider

   ```java
   package com.yafey.security.core.social.weixin.connect;
   
   /**
    * 微信的OAuth2流程处理器的提供器，供spring social的connect体系调用
    */
   public class WeixinServiceProvider extends AbstractOAuth2ServiceProvider<Weixin> {
   	
   	/**
   	 * 微信获取授权码的url
   	 */
   	private static final String URL_AUTHORIZE = "https://open.weixin.qq.com/connect/qrconnect";
   	/**
   	 * 微信获取accessToken的url
   	 */
   	private static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
   
   	/**
   	 * @param appId
   	 * @param appSecret
   	 */
   	public WeixinServiceProvider(String appId, String appSecret) {
   		super(new WeixinOAuth2Template(appId, appSecret,URL_AUTHORIZE,URL_ACCESS_TOKEN));
   	}
   
   	@Override
   	public Weixin getApi(String accessToken) {
   		return new WeixinImpl(accessToken);
   	}
   }
   ```

4. ApiAdapter

   ```java
   package com.yafey.security.core.social.weixin.connect;
   
   /**
    * 微信 api适配器，将微信 api的数据模型转为spring social的标准模型。
    *
    */
   public class WeixinAdapter implements ApiAdapter<Weixin> {
   	
   	private String openId;
   	
   	public WeixinAdapter() {}
   	
   	public WeixinAdapter(String openId){
   		this.openId = openId;
   	}
   
   	@Override
   	public boolean test(Weixin api) {
   		return true;
   	}
   
   	@Override
   	public void setConnectionValues(Weixin api, ConnectionValues values) {
   		WeixinUserInfo profile = api.getUserInfo(openId);
   		values.setProviderUserId(profile.getOpenid());
   		values.setDisplayName(profile.getNickname());
   		values.setImageUrl(profile.getHeadimgurl());
   	}
   
   	@Override
   	public UserProfile fetchUserProfile(Weixin api) {
   		return null;
   	}
   
   	@Override
   	public void updateStatus(Weixin api, String message) {
   		//do nothing
   	}
   }
   ```

5. OAuth2ConnectionFactory

   ```java
   package com.yafey.security.core.social.weixin.connect;
   
   /**
    * 微信连接工厂
    */
   public class WeixinConnectionFactory extends OAuth2ConnectionFactory<Weixin> {
   
   	public WeixinConnectionFactory(String providerId, String appId, String appSecret) {
   		super(providerId, new WeixinServiceProvider(appId, appSecret), new WeixinAdapter());
   	}
   	
   	/**
   	 * 由于微信的openId是和accessToken一起返回的，所以在这里直接根据accessToken设置providerUserId即可，不用像QQ那样通过QQAdapter来获取
   	 */
   	@Override
   	protected String extractProviderUserId(AccessGrant accessGrant) {
   		if(accessGrant instanceof WeixinAccessGrant) {
   			return ((WeixinAccessGrant)accessGrant).getOpenId();
   		}
   		return null;
   	}
   	
   	public Connection<Weixin> createConnection(AccessGrant accessGrant) {
   		return new OAuth2Connection<Weixin>(getProviderId(), extractProviderUserId(accessGrant), accessGrant.getAccessToken(),
   				accessGrant.getRefreshToken(), accessGrant.getExpireTime(), getOAuth2ServiceProvider(), getApiAdapter(extractProviderUserId(accessGrant)));
   	}
   
   	public Connection<Weixin> createConnection(ConnectionData data) {
   		return new OAuth2Connection<Weixin>(data, getOAuth2ServiceProvider(), getApiAdapter(data.getProviderUserId()));
   	}
   	
   	private ApiAdapter<Weixin> getApiAdapter(String providerUserId) {
   		return new WeixinAdapter(providerUserId);
   	}
   	
   	private OAuth2ServiceProvider<Weixin> getOAuth2ServiceProvider() {
   		return (OAuth2ServiceProvider<Weixin>) getServiceProvider();
   	}
   }
   ```

6. 配置SocialConfig

   ```java
   package com.yafey.security.core.social;
   
   @Configuration
   @EnableSocial  // 声明 使用 Spring Social
   public class SocialConfig extends SocialConfigurerAdapter {
   
   	@Autowired
   	private DataSource dataSource;
   	@Autowired
   	private SecurityProperties securityProperties;
   	
   	@Autowired(required = false)
   	private ConnectionSignUp connectionSignUp;
   
   	@Override
   	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
   		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource,
   				connectionFactoryLocator, Encryptors.noOpText());
   		repository.setTablePrefix("yafey_");  // 假设我们的 表名 规范里面有统一的前缀。
   		if(connectionSignUp != null) {
   			repository.setConnectionSignUp(connectionSignUp);
   		}
   		return repository;
   	}
   	
   	@Bean
   	public SpringSocialConfigurer yafeySocialSecurityConfig() {
   		String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
   		YafeySpringSocialConfigurer configurer = new YafeySpringSocialConfigurer(filterProcessesUrl);
   		configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl()); // 当找不到用户时，跳转到 注册页面。
   		return configurer;
   	}
   	
   	@Bean
   	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
   		return new ProviderSignInUtils(connectionFactoryLocator,
   				getUsersConnectionRepository(connectionFactoryLocator)) {
   		};
   	}
   
   }
   ```

7. WeixinAutoConfiguration

   ```java
   package com.yafey.security.core.social.weixin.config;
   
   /**
    * 微信登录配置
    */
   @Configuration
   @ConditionalOnProperty(prefix = "yafey.security.social.weixin", name = "app-id")
   public class WeixinAutoConfiguration extends SocialAutoConfigurerAdapter {
   
   	@Autowired
   	private SecurityProperties securityProperties;
   
   	@Override
   	protected ConnectionFactory<?> createConnectionFactory() {
   		WeixinProperties weixinConfig = securityProperties.getSocial().getWeixin();
   		return new WeixinConnectionFactory(weixinConfig.getProviderId(), weixinConfig.getAppId(),
   				weixinConfig.getAppSecret());
   	}
   	
   }
   ```

   

