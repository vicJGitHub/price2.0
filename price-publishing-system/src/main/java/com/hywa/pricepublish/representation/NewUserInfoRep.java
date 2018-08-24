package com.hywa.pricepublish.representation;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NewUserInfoRep {
	
	@NotNull(message="id不能为空")
	private String id;
	
	private String name;
	
	@NotNull(message="新密码不能为空")
	private String newpassword;
	
	@NotNull(message="旧密码不能为空")
	private String password;

}
