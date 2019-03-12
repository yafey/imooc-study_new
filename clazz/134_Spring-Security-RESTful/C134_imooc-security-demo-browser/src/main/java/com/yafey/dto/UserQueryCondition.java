package com.yafey.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class UserQueryCondition {
	private String username;
	private Integer age;
	private int ageTo;
	private String xxx;

}
