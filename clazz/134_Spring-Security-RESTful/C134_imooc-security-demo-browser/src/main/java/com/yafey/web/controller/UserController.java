package com.yafey.web.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonView;
import com.yafey.dto.User;
import com.yafey.dto.UserQueryCondition;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserController {

	@GetMapping("/users")
	@JsonView(User.UserSimpleView.class)
	public List<User> queryList() {

		//@formatter:off
		// List 链式 add (Collection 链式 add)
		// https://www.ctolib.com/mockcm-collection-chain.html
				// List<String> strList = ListBuilder.build(String.class)
				// .add("str1").add("str2")
				// .get();
				// 或者：
				// List<String> strList = new ListBuilder<String>()
				// .add("str1").add("str2")
				// .get();
		
				// Java 8 : 链式 add
				// https://www.geeksforgeeks.org/stream-builder-java-examples/
		
		Stream.Builder<User> builder = Stream.builder();
		List<User> userList = builder
				.add(new User()).add(new User()).add(new User())
				.build()
				.collect(Collectors.toList());
		// @formatter:on
		return userList;
	}

	@GetMapping("/usersWithParam")
	public User queryList(@RequestParam(name = "username", required = false, defaultValue = "defatul as tom") String nickname) {
		log.info("nickname:" + nickname);
		return new User().setUsername(nickname);
	}

	@GetMapping("/userCondition")
	public UserQueryCondition queryList(UserQueryCondition condition,
			@PageableDefault(page = 2, size = 17, sort = "username,asc") Pageable pageable) {
		
		log.info("/userCondition userQueryCondition:" + ReflectionToStringBuilder.toString(condition, ToStringStyle.MULTI_LINE_STYLE));
		log.info("Pageable:pageSize:{},pageNumber:{},sort:{}", pageable.getPageSize(), pageable.getPageNumber(),
				pageable.getSort());
		return condition;
	}

	@JsonView(User.UserDetailView.class)
	// URL 中使用 正则表达式，限制 用户 id 只能是 数字，
	@GetMapping("/user/{id:\\d+}")
	public User getUserDetail(@PathVariable(value = "id", required = true) Integer idxxx) {
		return new User().setId(idxxx);
	}

	@PostMapping("/user")
	public User createUser(@RequestBody User user) {
		user.setId(1);
		log.info(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
		return user;
	}

	@PutMapping("/user/{id:\\d+}")
	public User updateUser(@RequestBody User user) {
		log.info(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
		return user;
	}

	@DeleteMapping("/user/{id:\\d+}")
	public void deleteUser(@PathVariable(value = "id", required = true) Integer idxxx) {
		log.info("delete user , id:{}", idxxx);
	}
}
