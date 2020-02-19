// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Fans.java

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
	private Integer userId;
	private Integer fansId;
	@Transient
	private User user;

}
