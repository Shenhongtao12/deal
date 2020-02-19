package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;

@Data
public class Collect {
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private Integer userId;
	private Integer goodsId;

	@Transient
	private Goods goods;

}
