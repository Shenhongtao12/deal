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
	private String phone;
	private String sex;
	private String img;
	private String flag;

	@Transient
	private String code;
	@Transient
	private List<Role> roleList;


}
