package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;
@Data
public class Role
{
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String roleName;
	private String roleIntro;
	@Transient
	private List<Permission> permissionList;

}
