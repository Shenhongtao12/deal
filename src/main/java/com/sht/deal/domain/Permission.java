package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;

@Data
public class Permission
{
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String name;
	private String url;
}
