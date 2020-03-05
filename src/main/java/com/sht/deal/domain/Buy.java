package com.sht.deal.domain;


import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;

@Data
public class Buy {

	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String title;
    private String intro;
	private String weixin;
	private Double minPrice;
	private Double maxPrice;
	private String create_time;
	private Integer userid;

	private User user;

}
