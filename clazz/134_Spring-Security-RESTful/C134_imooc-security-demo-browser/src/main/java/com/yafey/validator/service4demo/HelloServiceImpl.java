package com.yafey.validator.service4demo;

import org.springframework.stereotype.Service;

// 注册为 Spring 容器的一个 Service。
@Service
public class HelloServiceImpl implements HelloService {

	@Override
	public String greeting(String name) {
		System.out.println("HelloServiceImpl greeting...");
		return "Hello " + name;
	}

}
