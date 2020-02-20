package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Data
public class Goods implements Serializable
{

	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String name;
	private String intro;
	private Double price1;
	private Double price2;
	private String images;
	private String weixin;
	private String create_time;
	private Integer state;
	private Integer classify2_id;
	private Integer userid;
	@Transient
	private boolean code;
	@Transient
	private Integer commentNum;
	@Transient
	private User user;
	@Transient
	private List commentList;

}
