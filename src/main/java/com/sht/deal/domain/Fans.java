package com.sht.deal.domain;


import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;

@Data
public class Fans {
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String createtime;
	private Integer userId;
	private Integer fansId;
	@Transient
	private User user;

}
