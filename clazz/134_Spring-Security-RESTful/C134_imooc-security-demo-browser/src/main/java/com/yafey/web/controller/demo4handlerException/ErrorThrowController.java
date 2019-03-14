package com.yafey.web.controller.demo4handlerException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.yafey.dto.User;

@RestController
public class ErrorThrowController {

	@GetMapping("/UserException4Demo/{id:\\d+}")
	public User getUserDetail(@PathVariable Integer id) {
		throw new UserException4Demo(id);
	}
}
