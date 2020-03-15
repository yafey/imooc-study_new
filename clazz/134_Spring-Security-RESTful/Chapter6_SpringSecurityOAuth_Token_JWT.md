---
typora-root-url: ./
typora-copy-images-to: ./README_images/6
---



[返回](./README.md)



# 第6章 Spring Security OAuth 开发 APP 认证框架

> 整理自 ： 
>
> [Spring Security源码分析十：初识Spring Security OAuth2](https://blog.csdn.net/dandandeshangni/article/details/79116294)
>
> [Spring Security源码分析十一：Spring Security OAuth2整合JWT](https://blog.csdn.net/dandandeshangni/article/details/79146127)
>
> https://blog.csdn.net/nrsc272420199/category_9090165.html



- 实现一个标准的 OAuth2 协议中 Provider 角色的主要功能。
- 重构之前的三种认证方式的代码，使其支持 Token 。
- 高级特性（Token 的生成方式（不使用默认的生成规则，如 JWT），SSO（使用 Token 如何实现单点登录）。

先讲一下如何搭建一个简单的认证服务器和资源服务器并简单走一下授权码模式和密码模式
研读一下spring security oauth源码,看如何将我们自定义的认证方式嫁接到spring security oauth框架上
将用户名+密码登陆、手机短信登陆和第三方登陆嫁接到spring security oauth框架上
利用jwt替换掉spring security oauth框架里默认实现的token



## 6.1(6-1). SpringSecurityOAuth 简介

> 本节如果熟悉 Session 和 cookie 的话， 可以跳过。

**从 cookie/session 和 token 的角度来认识一下 spring security oauth **



### 6.1.0. cookie 和 session

#### 6.1.0.1. cookie和session的概念

我想每一个web开发者肯定都知道cookie和session。但是在这里我仍然想用自己的话来表述一下。

- **啥是session**？

  简单理解它就是由服务器创建并存储于服务器的一个比较特殊的map — 除了可以像其他map一样存取数据，它还有过期时间、有一个唯一的id用于区分不同的session、而最最重要的是在创建该session时，会同时创建一个cookie，cookie的key为JSESSIONID，而cookie的value正是该session的id。

- **那啥又是cookie呢？** 

  首先需要指明的一点是cookie是由服务器创建的，它其实也可以简单的理解成一个map，或者更确切点来说就是一个k-v形式的键值对。当http请求获得到响应后，浏览器就会将服务端创建的cookie保存到浏览器中，默认情况下当浏览器关闭时该cookie就不存在了，但是服务器可以设置cookie的存活时间，如果设置了cookie的存活时间，即使关了浏览器，如果没超过cookie的存活时间，再打开浏览器，该cookie仍然会存在。—> 但是要说明的一点是服务器在创建session时，好像我们无法修改cookie的存活时间。

- 对`HttpSession session = request.getSession();`的深入理解。 作为java开发者应该都知道创建session时一般都会使用这个语句进行创建，但是人们通常对这句话的解释是如果服务器中有该request对应的session就取出，没有就新建一个session，却很少提及细节。我在这里试着展开说一下：

  - 首先服务器会遍历request中所有的cookie；

  - 如果某一个key为JSESSIONID的cookie，其value值正好与当前服务器中的某个session的id一致则取出该session；

  - 而如果服务器中没有session或者在服务器中找不到任何一个session的id与cookie中的value相同，则服务器就创建一个新的session，并同时创建一个key为JSESSIONID，value为该session的id的cookie并响应给浏览器。

    

#### 6.1.0.2. cookie、session在用户登陆中扮演的作用

   首先应该明确的一点是在没有cookie和session时，任意两次的http请求都是没有什么关系的，也就是大家平常说的http请求是无状态的。cookie和session为http之间的请求建立起了联系，具体的实现方式大体可分为如下几步：

- 用户登陆某个系统，在进行完身份认证后，该系统会在服务器上为这个用户创建一个session，并将认证后的用户信息保存到session里；

- 如 6.1.0.1 中所言，服务器在创建session的同时会创建一个key为JSESSIONID，value为该session id的cookie响应给浏览器；

- 这个用户的后续请求就会带着该cookie，服务器就可以通过请求中的cookie判定这些请求是该用户的请求。

  

### 6.1.1. token — 令牌

   至于为什么会出现token这种认证方式,以及它和cookie/session孰优孰劣等问题,这里不进行任何分析,请大家自行百度,这里只聊一下token的基本概念.

   那token究竟是什么呢？ 其实token就是由服务器生成的一个字符串,这里我大体按照自己的理解讲讲普通的token和JSON Web Token即jwt这两种token的生成机制及它们在用户登陆中扮演的作用.

#### 6.1.1.1. 普通的token

用户登陆某个系统，在进行完身份认证后，该系统会在服务器上为这个用户创建一个token，并将该token与用户信息进行绑定，并将该绑定关系存放在内存或者数据库 —> 同时将该token返回给浏览器；
浏览器会将登陆时获得到的token保存起来，并在后续的每次请求里都带着该token（token放在请求头里、参数里或者url里都行）；
服务器就可以通过请求中的token来判定这些请求是该用户的请求。
通过上面的描述可以看到这种token极其类似于session和创建该session时同步创建的cookie两者之和.

#### 6.1.1.2. JWT

用户登陆某个系统，在进行完身份认证后，该系统会在服务器上为这个用户创建一个jwt，该token虽然也是一个字符串,但是它却又比较特殊,它里面包含用户信息(实际生产中密码肯定都不会放到该token里)、用户权限信息等 —> 并且服务器不会存储该token，而是将生成的token返回给浏览器；
浏览器会将登陆时获得到的token保存起来，并在后续的每次请求里都带着该token（token放在请求头里、参数里或者url里都行）；
服务器从请求中取出token —> 验证该token是不是本服务器发送给该用户的有效的token，----> 并以此来判定是不是该用户的请求，且该请求有没有权限访问该服务器的api。



### 6.1.2. 认识一下spring security oauth

#### 6.1.2.1. 什么是spring security oauth

   其实前面文章中介绍的用户名+密码登陆、短信登陆、第三方用户登陆都是基于cookie和session实现的，那如何做基于token的登陆认证呢？按照本文关于token的介绍，我觉得自己实现一个普通token的生成、存储、发送给浏览器(或app)+验证随后请求中的token属于哪个用户这一套流程应该也不是不可能的。但更简单且可取的方式是我们`使用别人造好的轮子` —> spring security oauth。

   <span style="color:green">**那spring security oauth是什么呢？**</span> 

> 简单点说就是：封装了 服务提供商 的 行为，负责 发放令牌 ，校验令牌。

   首先我们先看一下下面这幅图，想必看过我前面关于第三方登陆的文章的人肯定不陌生。之前讲第三方登陆时，我们的程序相当于第三方应用，QQ、微信等是服务提供商。当我们的服务需要请求服务提供商的某一个接口（获取用户信息的接口）时，我们需要先向它们的认证服务器申请token，然后拿着token去它们的资源服务器去请求它们的接口 —> 再之后，它们的资源服务器会拿着请求中的token去认证服务器进行验证该token是否合法有效 —> 如果是,我们的应用就可以调用到它们接口并获取到相应的数据了。

- 在第三方登陆的过程中我们真正能操作的是我们的应用（引导用户进行授权并去认证服务器获取token、拿着token获取用户信息等） —> 使用的技术栈为spring social。
- 而服务提供商中认证服务器生成token并返回给第三方应用、在第三方应用请求资源服务器的接口时先去认证服务器校验请求中的token是否合法有效、验证token合法有效后第三方应用才能请求到资源服务器的接口数据 —> 这一整套流程正是spring security oauth所解决的问题。



**稍微总结一下**

- springsocial主要是对oauth2协议中的资源所有者、第三方应用两个角色行为的封装;
- spring security oauth主要是对oauth2协议中服务提供商、认证服务器和资源服务器三个角色行为的封装。

![image-20200314102553150](README_images/6/image-20200314102553150.png)



### 6.1.2. spring security oauth开发基于token的认证方式 原理介绍

spring security oauth的大致原理如下图。

- 认证服务器
  - 要实现 `4 种授权模式` --- 即 简单模式，授权码模式，客户端模式，密码模式。
    - spring security oauth 已经对上述 4 种模式 提供了 默认的实现。
  -  根据 某种授权下 获取到的 用户信息， 进行 token 的 生成、存储。根据 token 中权限去获取 资源服务器上的 资源。
    - oauth协议中并没有规定token要怎么生成、存储和校验。spring security oauth提供了一个默认的实现,其基本原理`大致`就是我在本文描述的普通的token。我会在接下来的文章里探索如何将其换成jwt。

- 资源服务器

  - 保护资源 --- 保护我们的 rest 服务。

  - Spring Security 通过 过滤器链 来保护 资源。

    - 那么 Spring Security OAuth 是如何来实现 资源服务器 的 功能呢？

      它是通过在 过滤器链上 增加了 OAuth2AuthenticationProcessingFilter 过滤器。它的作用是 从请求中拿到 发出去的 token ，然后根据 token 对应的 用户 及对应的权限 来判断是否能访问 rest 服务。

> 但是开发基于token的认证，用户走的不是 4 种 标准授权模式中的任何一种,因为我们的目的就是在用户登陆时,如果用户信息校验(*这里所说的用户校验其实就是指的我们前面文章里讲到的用户名+密码登陆、手机短信登陆和第三方登陆*)成功了,我们就为该用户生成一个token并将其返回给用户 —> 用户接下来的每次请求再带着该token —> 我们只需要验证该token合法 —> 就可以让用户请求到我们接口的数据了。
>
> 所以这一块需要我们自己实现。

![image-20200314214452907](README_images/6/image-20200314214452907.png)





## 6.2(6-2). 实现一个标准的 OAuth2 协议中 Provider(服务提供商)

> 主要实现 认证服务器 和 资源服务器。