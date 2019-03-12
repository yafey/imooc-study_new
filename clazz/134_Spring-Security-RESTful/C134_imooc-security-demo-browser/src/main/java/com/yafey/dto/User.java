package com.yafey.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

	public interface UserSimpleView {};
	public interface UserDetailView extends UserSimpleView {};

	private Integer id;
	private String username;
	private String password;

	@JsonView(UserSimpleView.class)
	public String getUsername() {
		return username;
	}

	@JsonView(UserDetailView.class)
	public String getPassword() {
		return password;
	}
	
	@JsonView(UserSimpleView.class)
	public Integer getId() {
		return id;
	}

}
