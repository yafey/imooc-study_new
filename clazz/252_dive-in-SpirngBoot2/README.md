---
typora-root-url: ./
typora-copy-images-to: ./README_images/
---


# Spring Boot 2.0深度实践之核心技术篇

## 0. 补充内容

### 微服务： 一般指的是 「微服务架构」[^quick-understand-microservice] [^microservice-tech-stack]

> 2014 年可以认为是微服务 1.0 的元年，当年有几个标志性事件，<br/>
> 一是 <span style="color:green">**Martin Fowler 在其博客上发表了”Microservices”一文**</span>，正式提出微服务架构风格；<br/>
> 二是 Netflix 微服务架构经过多年大规模生产验证，最终抽象落地形成一整套开源的微服务基础组件，统称 NetflixOSS，Netflix 的成功经验开始被业界认可并推崇；<br/>
> 三是 Pivotal 将 NetflixOSS 开源微服务组件集成到其 Spring 体系，推出 Spring Cloud 微服务开发技术栈。<br/>



[^quick-understand-microservice]: http://dockone.io/article/3687 一篇文章快速理解微服务架构 @ MARCH 06, 2018 <br/>

[^microservice-tech-stack]: https://infoq.cn/article/micro-service-technology-stack  微服务架构技术栈选型手册@2018.02.12





### 微服务 与 Spring Boot

两者的趋势是同步的， Spring Boot 的搜索量更大， 毕竟 开发人员更关心 「如何落地 微服务架构」。

![1553323927487](README_images/1553323927487.png)

为什么 Spring Boot 会成为 微服务架构 的首选？

1. 绝对的市场占有率 

   > Spring Boot 基于 Spring Framework ， Rebel 实验室 在 2017 年 不完全统计的 数据如下，而且 Spring 架构体系完全兼容 Java EE ， 所以真实的市场占有率应该是 Spring + Java EE。 
   >
   > 有一种观点：<span style="color:green">「 **Spring 已经成为 Java 的事实标准。**」</span> ，长期处于绝对的领先地位。

2. 优秀的家族基因

   > Spring Boot 在 Spring 体系中， 起到了承上启下的角色，为 底层的 Spring Framework 快速搭建应用 ； 为 高层的 Spring Cloud 提供了 基础设施。 Spring 官方给它起了 「BUILD ANYTHING」这样的 slogan 来体现它的家族地位。

3. 与时俱进的技术信仰

   > Spring Boot 1.x 时代 ，构建在 Java EE 的技术体系，常见的 Spring MVC ，属于 Servlet 的技术栈。 数据层可选用 JDBC、JPA 等规范。
   >
   > Spring Boot 2.x 时代， 将 Java EE 从以前的 必选项 调整为 「可选项」， 可能的原因是 SUN 公司的倒塌，Oracle 对 Java EE 的热情骤减，可能终将抛弃 。 Spring 家族的发展蒸蒸日上，并且以广阔的胸襟拥抱业界的变化，最为显著的是编程模型的变化，其中以 「Reactive 为代表的编程范式」—— 使用 **非阻塞 加 异步 的方式**，帮助应用充分地利用系统资源，提高应用伸缩性。同时，底层的数据存储方面，已经提升到了 常见的核心的基础设施 ，包括 MongoDB 、Redis 等。
   >
   > <span style="color:green"> **使用 Reactive 方式 来实现 非阻塞 异步的 编程 将成为接下来的主流的编程方式。**</span> Spring 5 / Spring Boot 2.x 提早布局， 等待趋势变为现实。


![1553327694093](README_images/1553327694093.png)





## 第1章 系列总览

总览 Spring Boot 2.0 深度实践系列课程的整体议程，包括 Spring Boot 三大核心特性（组件自动装配、嵌入式Web容器、生产准备特性）、Web 应用（传统 Servlet、Spring Web MVC、Spring WebFlux）、数据相关（JDBC、JPA、事务）、功能扩展（SpringApplication、Spring Boot 配置、Spring Boot Starter）以及...



## 1.1. 总览

- 全面覆盖： 本课程做不到面面俱到，而是在某个领域全面覆盖到，了解来龙去脉，相关原理，以及 源码分析。

- 重点突出： Spring Boot 的核心特性，主要关注 **Spring Boot 和 Spring Framework 及 Java EE 规范之间的联系**。 （Spring Boot 整合的一- 些不常用操作，并不是一定要用 Spring 的 实现。）



![1553333884471](README_images/1553333884471.png)



带着问题学习

- Spring Boot 如何基于 Spring Framework 逐步走向 自动装配的？
  - Spring Framework 1.x ~ 5.x 都有不同程度的 自动化装配的 贡献， 如 ： 模式注解等。
- SpringApplication 是如何掌控 Spring 应用生命周期的？
  - 传统的 Servlet 应用 是由 Servlet 容器 来驱动的， Servlet 容器启动后 再启动 SpringApplication 的上下文 （SpringApplicationContext）。
  - SpringBoot 1.x 开始， Spring boot 来启动 SpringApplication 的上下文。



![1553334745445](README_images/1553334745445.png)

## 1.2. Spring Boot 2 易学难精

- <span style="color:green">**Spring Boot 2 易学** </span>
- - **组件自动装配**：规约大于配置，专注核心业务
  - **外部化配置**：一次构建、按需调配，到处运行
  - **嵌入式容器**：内置容器、无需部署、独立运行
  - **Spring Boot Starter**：简化依赖、按需装配、自我包含
  - **Production-Ready**：一站式运维、生态无缝整合


- <span style="color:red;">**Spring Boot 2 难精** </span>

  - **组件自动装配**：模式注解、@Enable模块、条件装配、加载机制

    - *Spring Boot 在 Spring Framework 的基础上， 扩展了一些 Spring Boot Annotation。*
  - **外部化配置**：Environment抽象、生命周期、破坏性变更 

    - *Spring Boot1.0 和 Spring Boot2.0之间有一点不太兼容的，在迁移和学习时，应予以高度关注。*
  - **嵌入式容器**：Servlet Web容器、Reactive Web容器

    - Reactive Web容器 --> Netty
  - **Spring Boot Starter**：依赖管理、装配条件、装配顺序
  - **Production-Ready**：健康检查、数据指标、@Endpoint管控


![1553335864128](README_images/1553335864128.png)



### 了解 Spring Boot 与 Java EE 规范 的联系与区别

> 精通 Spring Boot 的 学习建议， 多了解 Spring 源码 与 JSR ( Java EE 规范 ) 的关系。


- **Web** ： Servlet （JSR-315 、JSR-340）

  - JSR-315: Servlet 3.0 规范，包括 异步化。
  - JSR-340: Servlet 3.1 规范，包括 非阻塞。 WebFlux 兼容 Servlet 3.1 容器，官网简言之，但是我们不能放过任何的讯息。

- **SQL** ： JDBC （JSR-221） ： JDBC 4.0 规范。

- **数据校验**： Bean Validation ( JSR-303 、 JSR-349) 。Spring Boot 2 其实实现的是 JSR-349 , 但是官方文档中写的还是 JSR-303 。

  -  JSR-303 ：Bean Validation1.0。
  -  JSR-349：Bean Validation1.1 。

- **缓存**：Java Caching API ( JSR-107 ).

- **WebSockets** ： Java API for WebSocket ( JSR-356 )

  Spring 并不提供任何的实现，它的实现是落在 Tomcat 这样的 Servlet 容器上，因此 Tomcat 容器是实现了 WebSockets 的规范，这是 Spring 和Java EE 的困境。

- **Web Services** : JAX-WS （JSR-224）, 微服务中用的比较少。

- **Java 管理** ： JMX  （ JSR 3）。

  Java 管理 的扩展 JMX，Spring Boot 的核心概念 Production-Ready（为生产级别而准备的一些特性），包括 `@Endpoint` 端点，端点提供了两种方式，一种是 Web 方式来进行访问，另一种就是 JMX，Spring Boot 和 JMX 整合的是比较好的 。

- **消息** ： JMS ( JSR-914)  ，主要是指 JMS的规范（ JMS1.1 ），其中有代表性的实现就是 `Active MQ` 。



​	其实，这里举的例子只是很小的一部分，Spring Framework 和 Spring Boot 在实现 Java EE 规范方面是花了非常多的心思的，Java EE 规范是一个大而全的技术体系栈。Spring Boot 在此基础上提升了它的开发效率，并且简化了部分复杂的 API，因此我们需要搞清楚 Spring Boot 和 Java EE 规范之间的联系。

![1553347986389](README_images/1553347986389.png)





## 1.3. Spring Boot深度实践-总览

> 核心特性、Web应用（引入了Reactive Web的开发，就是我们常说的Web Flux，这是对Spring MVC或Servlet应用是一种补充，因为Spring MVC是构建在Servlet基础上的实现）、数据相关（JDBC、JPA相关的操作）、功能扩展（Web化配置、自动装配、嵌入式容器都需要对Spring Boot API非常熟悉，尤其像Spring Boot嵌入式容器这块，2.0和1.0的实现方式也发生了变化）、运维管理（为生产而准备的特性，以运维为主，日志监控、性能监控以及核心的业务指标监控，Spring Boot提供了一套完整的解决方案）。



- **核心特性** ：
  - Spring Boot 1.x (1.0~1.5) 与 Spring Boot 2 跨度比较大， 转换时的 注意事项。
- **Web 应用** ： 
  - Spring Boot 2 基于 Spring framework 5.0 ( 引入 Reactive Web 开发， 即 WebFlux ) 。
- **数据相关** ： JDBC、JPA 。
- **功能扩展**： 自动装配、配置化、嵌入式容器 等 ， Spring Boot API 的熟悉， Spring Boot 1.x 与 2.x 中 对 嵌入式容器的实现有所差异。
- **运维管理**：Ready for Production。
  - 日志监控、性能监控、核心的业务指标监控 等。
  - 国外著名的 Spring Admin 等基于这块来做的。



## 1.4. (1-4 ~ 1-5)核心特性介绍(Spring Boot三大特性)

1. **组件自动装配**：

   「组件自动装配」可以分成 「组件」、「自动」、「装配」三部分。 

   - 「组件」 就是 Web MVC、Web Flux、JDBC、Transactions事务 等。

   - 在 Spring Framework 时代， 上面提及的这些「组件」已经有了成型的方案。

     Spring Boot 在此基础上，将原先的 手动配置的方式 变成了 「自动」的方式。

   - 减少了代码的编写，并且按照一定条件的方式触发( 「装配」 )，更好地理解一些特性，**使得我们可以更专注于业务开发，减少框架配置调试的时间**。

2. **嵌入式 Web 容器**：Spring Boot 中可以自由切换下面的两种不同的容器。

   - 传统 Servlet 容器： Tomcat、Jetty 以及 Undertow( JBoss ) 等。
   - Reactive Web容器 ( Web Flux )  : 基于 Netty 开发的 HTTP Server 服务器（当然， Web Flux 也可以基于 Servlet3.1 API 来实现）。

3. **生产准备特性**（`Production-Ready`）：提供一些非功能特性，主要是 监控 和 管理 等 特性。

   > 功能性特性 如 Web Flux、Web MVC 以及 JDBC 等都是为开发做准备的 。
   >
   > URL中的前缀 `/actuator` 是在 Spring Boot 2.x 中新增的（也可以修改）， Spring Boot 1.x 中不需要这个前缀。

   - **指标(Metrics)** ： `/actuator/metrics` （CPU、内存、磁盘的利用率） 。

   - **健康检查(Health Check)** : `/actuator/health（应用是否健康、数据库、磁盘、文件系统是否健康）`

   - **外部化配置(Externalized Configuration)** ：`/actuator/configprops`（不用写代码可以调整应用的行为，比如 port 端口）。

     「外部化配置」在 Spirng Boot 工程里用的非常广泛，然而它的本质是来自于 Spring Framework，只不过在 Spring Framework 中不叫 「外部化配置」，Spirng Boot 把这个概念 加以提升了。

     > 「外部化配置」也可以通过 endpoint 进行暴露，也就是说它可以通过调整配置的方式修改应用的行为。

![1553355148040](README_images/1553355148040.png)



### 1.4.1. (1-5)组件自动装配介绍

>激活： `@EnableAutoConfiguration`
>配置： `/META-INF/spring.factories`
>实现： `XXXAutoConfiguration`

真正的实现比较复杂，但是可以简要概括为以下几步：

1. **激活**：使用 `@EnableAutoConfigureation` 注解。

   言外之意，<span style="color:red">Spring Boot 默认是 没有 激活 自动装配功能的</span> 。 

2. **配置**：`/META-INF/spring.factories`。

   这是一个规约文件，既可以是它内部的实现，也可以是用户自定义的实现。

   `/META-INF` 是元信息的目录，`.factories` 是工厂机制，类似 `key-value` 的形式。

3. **实现**：`XXXAutoConfiguration`。

   `XXX`代表一种特性，比如 `WebMVCAutoConfiguration`、`JDBCAutoConfiguration`。



#### 示例项目—组件自动装配

> 说明 Spring Boot 中如何处理这些激活方式 , 以及 配置文件间的 交互、过程。

1. 新建 Spring Boot 项目。

   ~~通过 http://start.spring.io 生成，然后 Import Maven 项目。~~ 使用 IDE 创建 Spring Boot 项目。

2. 官网的示例中 使用 `@EnableAutoConfiguration` 注解，项目中使用的 `＠SpringBootApplication` 注解上标注了  `@EnableAutoConfiguration` 注解 。

3. 在 IDEA 中按 `Ctrl+Shift+N`，输入`spring.factories`，选择`spring-boot-autoconfigure-2.1.2.RELEASE.jar` 。

4. 在这里，我们清楚地看到 `org.springframework.boot.autoconfigure.EnableAutoConfiguration` （全类名 或 接口名）做为 key ，它的 value 是各种各样的实现类。Spring 在启动时会启动很多的 自动装配的 bean （实现类）。

5.  常用的 `WebMvcConfiguration` 依赖 Servlet、DispatcherServlet 等，<span style="color:red">而这些东西我们工程里没有，所以它是不会被装配起来的，它的功能将加以阻止。</span>

   **其实自动化装配很复杂**，如 `WebMvcConfiguration` **有很多 前提条件 和 相应的顺序**。



```java
package com.yafey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiveInSpirngBoot2Application {

	public static void main(String[] args) {
		SpringApplication.run(DiveInSpirngBoot2Application.class, args);
	}

}
```

![1553362177484](README_images/1553362177484.png)



## 1.5. (1-6)Web 应用介绍

### 1.5.1. 传统 Servlet 应用

1. **`Servlet 组件`**：Servlet、Filter、Listener。

   常见的 Listener 有 ：ServletContextListener、RequestListener。

2. **`Servlet 注册`**：Servlet 注解、Spring Bean、RegistrationBean

   - `Servlet 注解` 其实是 **Servlet 3.0 本身提供的特性**。
   - `Spring Bean` 的注册方式，**在 Spring Boot 里面，我们允许把 Servlet 部署成一个Spring 的 Bean 加以 加载 和 映射**。
   - `RegistrationBean` 这种方式，**是 Spring Boot 提供新的 API**，有几种不同的变种。

3. **异步非阻塞**：异步 Servlet、非阻塞 Servlet 。

   主要是依靠 Servlet3.0 以上 的 API 来进行实现的。

   - 异步 Servlet 主要是 Servlet3.0 提供的一种实现。
   - 非阻塞 Servlet 是 Servlet3.1 的实现。
   - 因此，在 Web Flux 里面，它有一个地方提到了：**Web Flux 可以运行在 Servlet3.1 以上的 API，也说是说 Serlvet3.1 里面其实有包含异步和非阻塞两个特性**。



#### 1.5.1.0. (1-7)传统Web应用示例 TL;DR

1. **添加传统 Web 依赖** （Spring Boot Starter 方式引入）

   ```xml
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ```

2. **Servlet 组件**

   - **Servlet**
     1. 实现
        - `@WebServlet`
        - `extends HttpServlet`
        - 重写方法（`@Override`）
     2. URL 映射
        - `@WebServlet(urlPatterns="/my/servlet")`
     3. 注册
        - `@ServletComponentScan(basePackages="com.imooc.diveinspringboot.web.servlet")`
   - **Filter**
   - **Listener**

3. **Servlet 注册**

   1. 方式一：**Servlet 注解**
      - `@ServletComponentScan +`
        1. `@WebServlet`
        2. `@WebFilter`
        3. `@WebListener`

   2. 方式二：**Spring Bean**
      - `@Bean +`
        1. Servlet
        2. Filter
        3. Listener
   3. 方式三：**RegistrationBean**
      - `ServletRegistrationBean`
      - `FilterRegistrationBean`
      - `ServletListenerRegistrationBean`

4. **异步非阻塞**

   - **异步 Servlet**
     - `javax.servlet.ServletRequest#startAsync()`
     - `javax.servlet.AsyncContext`
   - **非阻塞 Servlet**
     1. `javax.servlet.ServletInputStream#setReadListener`
        - `javax.servlet.ReadListener`
     2. `javax.servlet.ServletOutputStream#setWriteListener`
        - `javax.servlet.WriteListener`



#### 1.5.1.1. 传统Web应用示例—详细

##### 添加传统 Web 依赖

**Spring Boot 的 自动装配** 其实是采用 「 `spring-boot-starter` 方式」。

- `spring-boot-starter` 分成 很多功能模块，如 `spring-boot-starter-test`、`spring-boot-starter-web` 模块。
- 这些模块的 `groupId` 都是 `org.springframework.boot`，而 `artifactId` 都是以 `spring-boot-starter-xxx` 开头的。



添加 传统 Web 依赖后：( 添加该依赖后， 我们可以看到一些 间接依赖，这里的有 tomcat、JSON、Servlet API 等 间接依赖。 )

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

启动 Spring Boot 应用，我们可以看到启动的容器是 Tomcat，端口是 8080，和上面的包依赖 tomcat 遥相呼应，当输入`localhost:8080`后，<span style="color:red">**会出现一个白页 ( Whitelabel Error Page )  ,因为当前 没有增加 映射的 任何处理**</span>，也就是说 当前页面没有映射到任何一个处理的实现类，这个白页是错误信息的警告，因此会报 404 的错误。



##### Servlet 注解方式 实现 传统 Servlet3.0 。

> `@ServletComponentScan` + `@WebServlet`( Servlet3.0 )
>
> **传统的 SpringMVC 应用也是可以放到 Spring Boot 里面的。**

示例: 见 提交 [4b2f9ec](/../../commit/4b2f9ec)

1. 模仿 `org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration` 类 创建 `web.servlet.MyServlet` class。

   - **Spring Boot2 的 web 有两种实现方式 ： Servlet 和 Reactive** ， 所以 WebMvcAutoConfiguration 类的全路径 也 从 `web.WebMvcAutoConfiguration` 调整为 `web.servlet.WebMvcAutoConfiguration`。

2. 添加 `@WebServlet`，`extends HttpServlet` (Servlet3.0 规范里决定) ，覆盖 `doGet(HttpServletRequest req, HttpServletResponse resp)` 方法。

3. 添加 URL 映射 ： `@WebServlet(urlPatterns="/my/servlet")`

4. 注册 : `@ServletComponentScan(basePackages="com.imooc.diveinspringboot.web.servlet")`。

   有点类似于 `@ComponentScan`。

5. 启动应用后，在浏览器输入 http://localhost:8080/my/servlet 后，能正常获得 “Hello,World” 信息。

6. 这里演示的 Servlet 的注册方式 是 Spring 自定义的 注册方式 (`@ServletComponentScan` +  `@WebServlet`( Servlet3.0) ) ，它结合了 Servlet 规范来操作的。

   - 和 `@WebServlet` 相应的 ， 还有 `@WebFilter` 和 `@WebListener` 。
7. 另外还有 Spring Beant 和 RegistrationBean 两种方式，Spring Bean 和 RegistrationBean 方式有点类似。后续加以说明。



![1553522316612](README_images/1553522316612.png)



### 1.5.2. (1-8)异步非阻塞 Servlet

异步非阻塞分两个方面，异步Servlet ( 3.0中提出 )，非阻塞Servlet (3.1中提出)。

1. **异步Servlet**
   - `javax.servlet.ServletRequest#startAsync()`
   - `javax.servlet.AsyncContext`
2. **非阻塞Servlet**
   - `javax.servlet.ServletInputStream#setReadListener`
     - `javax.servlet.ReadListener`
   - `javax.servlet.ServletOutputStream#setWriteListener`
     - `javax.servlet.WriteListener`



> <span style="color:red;">注意点：异步操作 比 同步操作 要复杂很多。</span>  示例: 见 提交 [待补充](/../../commit/4b2f9ec)
>
> 1. <span style="color:green">**需要配置 `@WebServlet` 的 属性 `asyncSupported = true`**</span> , **不然会报错**。完整的注解为：  `@WebServlet(urlPatterns="/my/servlet",asyncSupported=true)` 
> 2. 在业务代码完成后，<span style="color:green">**需要显式地 触发完成 `asyncContext.complete();`**  </span> ， 不然，浏览器会一直等待服务器端响应，直到达到 超时时间。

![1553526964077](README_images/1553526964077.png)