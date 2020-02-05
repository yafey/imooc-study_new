package com.yafey.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class UserQueryCondition {
	private String username;
	@ApiModelProperty(value="用户年龄起始值")
	private Integer age;
	@ApiModelProperty(value="用户年龄终止值")
	private int ageTo;
	private String xxx;

}
