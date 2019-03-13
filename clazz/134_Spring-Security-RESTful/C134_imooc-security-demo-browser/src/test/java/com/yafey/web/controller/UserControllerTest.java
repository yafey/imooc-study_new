package com.yafey.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	// Before 注解会在 所有 Test cases 之前 执行， 一般用于 环境初始化。
	public void setup() {
		// 伪造 MVC 环境，不用真正的启动 Tomcat。
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void whenQueryUserListSuccess() throws Exception {
		// 用伪造的 MVC 环境，模拟发送一个 request ， 作为 perform 方法的参数， 执行后，校验结果是否符合预期。
//		mockMvc.perform(MockMvcRequestBuilders.get("/user")
//				.contentType(MediaType.APPLICATION_JSON_UTF8))
//			.andExpect(MockMvcResultMatchers.status().isOk()) // 判断返回的 status =200 ， 成功。
//			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3));  //  jsonPath 语法 见 https://github.com/json-path/JsonPath

		// 添加 Favorite 之后， 自动 static import .
		// @formatter:off
		String result = mockMvc.perform(get("/users")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(3))
			.andReturn().getResponse().getContentAsString()
			; 
		// @formatter:on
		log.info("result:{}", result);
	}

	@Test
	public void whenQueryUserWithParamSuccess() throws Exception {
		// @formatter:off
		mockMvc.perform(get("/usersWithParam")
				.param("username", "yafey")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			; 
		// @formatter:on
	}

	@Test
	public void whenQueryUserConditionSuccess() throws Exception {
		// @formatter:off
		mockMvc.perform(get("/userCondition")
				.param("username", "yafey")
				.param("age", "18")
				.param("ageTo", "60")
				.param("xxx", "yyy")
				
				// 下面 3 个参数 可以自动绑定到 Spring Data 中的 Pagable 对象上。
				// 如果指定 @PageableDefault注解 也可以不传下面的参数 :  @PageableDefault(page=2,size=17,sort="username,asc") Pageable pageable
//				.param("size", "15")
//				.param("page", "3")
//				.param("sort", "age,desc")
				
				.contentType(MediaType.APPLICATION_JSON_UTF8)
			)
			.andExpect(status().isOk())
			; 
		// @formatter:on
	}

	@Test
	public void whenGetUserDetailSuccess() throws Exception {
		// @formatter:off
		String result = mockMvc.perform(get("/user/1")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andReturn().getResponse().getContentAsString()
				;
		// @formatter:on
		log.info("result:{}", result);
	}

	@Test
	// url 中使用 正则表达式，限制 用户 id 只能是 数字，非数字返回 4xx 状态码。
	public void whenGetUserDetailFail() throws Exception {
		// @formatter:off
		mockMvc.perform(get("/user/a")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
				.andExpect(status().is4xxClientError())
				;
		// @formatter:on
	}

	@Test
	public void whenCreateSuccess() throws Exception {
		Date date = new Date();
		String content = "{\"username\":\"user1\",\"password\":\"1\",\"birthday\":" + date.getTime() + "}";
		log.info("content:{}", content);
		// @formatter:off
		String result = mockMvc.perform(
					post("/user")
					.content(content)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andReturn().getResponse().getContentAsString()
				;
		// @formatter:on
		log.info("result:{}", result);
	}
	
	
	@Test
	// 因为 User 的 password 上有 @NotNull 注解， Controller 上也加了 @Valid 注解，
	// 如果 password 为 null ， 就会得到 400 的错误。
	public void whenCreateFailed() throws Exception {
		// 明年的今天
		Date date = new Date(LocalDateTime.now().plusYears(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		String content = "{\"username\":\"user1\",\"password\":null,\"birthday\":" + date.getTime() + "}";
		log.info("content:{}", content);
		// @formatter:off
		mockMvc.perform(
					post("/user")
					.content(content)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
				.andExpect(status().is4xxClientError())
				;
		// @formatter:on
	}

	@Test
	public void whenUpdateSuccess() throws Exception {
		String content = "{\"id\":\"1\",\"username\":\"user1\"}";
		log.info("whenUpdateSuccess content:{}", content);
		// @formatter:off
		String result = mockMvc.perform(
					put("/user/1")
					.content(content)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andReturn().getResponse().getContentAsString()
				;
		// @formatter:on
		log.info("whenUpdateSuccess result:{}", result);
	}
	
	@Test
	// Spring 会将错误 绑定到 BindingResult 对象上， 不像 @Valid 注解那样 方法体都不执行。
	// 代码中可以拿到 如下的 错误信息。
	// 默认的错误信息 ：  bindingErrors:[birthday] must be in the past; [password] may not be empty
	// 自定义的错误信息： bindingErrors:[birthday] 生日必须是过去的时间; [password] 密码不能为空
	public void whenUpdateFailed() throws Exception {
		// 明年的今天
		Date date = new Date(LocalDateTime.now().plusYears(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		String content = "{\"id\":\"1\",\"username\":\"user1\",\"password\":null,\"birthday\":" + date.getTime() + "}";
		log.info("whenUpdateFailed content:{}", content);
		// @formatter:off
		String result = mockMvc.perform(
					put("/user/1")
					.content(content)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andReturn().getResponse().getContentAsString()
				;
		// @formatter:on
		log.info("whenUpdateSuccess result:{}", result);
	}

	
	@Test
	public void whenDeleteSuccess() throws Exception {
		// @formatter:off
		mockMvc.perform(
					delete("/user/1")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
				)
				.andExpect(status().isOk())
				;
		// @formatter:on
	}
}
