package com.yafey.dto;

import java.util.Date;
import javax.validation.constraints.Past;
import org.hibernate.validator.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonView;
import com.yafey.validator.MyConstraint;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

	public interface UserSimpleView {};
	public interface UserDetailView extends UserSimpleView {};

	private Integer id;
	@MyConstraint(message="自定义校验器测试，因为在校验器的 isValid 方法直接返回 false，肯定会-->看到本消息就是校验失败了。。。")
	private String username;
	@NotBlank(message="密码不能为空")
	private String password;
	@Past(message="生日必须是过去的时间")
	private Date birthday;

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
	
    @JsonView(UserSimpleView.class)
    public Date getBirthday() {
        return birthday;
    }
}
