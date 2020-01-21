package com.yafey.dto;

import java.util.Date;

import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User2 {

	private Integer id;
	private String username;
	@NotBlank(message="密码不能为空")
	private String password;
	@Past(message="生日必须是过去的时间")
	private Date birthday;

}
