package com.yafey.web.controller.demo4handlerException;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserException4Demo extends RuntimeException {

	private static final long serialVersionUID = 4624103203377628892L;
	
	private Integer id;

	public UserException4Demo(Integer id) {
		super("User not exist.");
		this.id = id;
	}
}
