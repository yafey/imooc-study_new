package com.yafey.dto;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

	private Integer id;
	private String username;
	private String password;
	private Date birthday;
}
