package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;

@Data
public class Love {

	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String type;
	private Integer typeid;
	private Integer userid;
	private String createtime;

	@Transient
	private User user;

}