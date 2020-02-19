// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Goods.java

package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

// Referenced classes of package com.sht.deal.domain:
//			User
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
	private User user;
	@Transient
	private List commentList;

}
