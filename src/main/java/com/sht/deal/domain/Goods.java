package com.sht.deal.domain;

import io.swagger.annotations.ApiModelProperty;
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
	// 0 展示 1 待审批或下架 2 拒绝
	private Integer state;
	private Integer classify2_id;
	private Integer userid;
	@ApiModelProperty(name = "type",notes = "1: goods, 2: post")
	private String type;
	@Transient
	private boolean code;  //判断用户是否收藏该产品
	@Transient
	private Integer commentNum;  //留言条数
	@Transient
	private User user;
	@Transient
	private List commentList;


}
