package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Data
public class User
{
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String username;
	private String email;
	private String password;
	private String nickname;
	private String intro;  //介绍
	private String phone;
	private String sex;
	private String img;
	private String flag; //判断是否修改过用户名，只能修改一次

	@Transient
	private String code;//邮箱验证码
	@Transient
	private List<Role> roleList;


}
