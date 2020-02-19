// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JsonData.java

package com.sht.deal.utils;

import java.io.Serializable;

public class JsonData
	implements Serializable
{

	private static final long serialVersionUID = 1L;
	private Integer code;
	private Object data;
	private String msg;

	public JsonData()
	{
	}

	public JsonData(Integer code, Object data, String msg)
	{
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	public static JsonData buildSuccess()
	{
		return new JsonData(0, null, null);
	}

	public static JsonData buildError(Object data, String msg)
	{
		return new JsonData(-1, data, msg);
	}

	public static JsonData buildError(String msg)
	{
		return new JsonData(-1, null, msg);
	}

	public static JsonData buildError(String msg, Integer code)
	{
		return new JsonData(code, null, msg);
	}

	public static JsonData buildSuccess(Object data, String msg)
	{
		return new JsonData(0, data, msg);
	}

	public static JsonData buildSuccess(String msg)
	{
		return new JsonData(0, null, msg);
	}

	public Integer getCode()
	{
		return code;
	}

	public void setCode(Integer code)
	{
		this.code = code;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public String toString()
	{
		return "JsonData [code=" + this.code + ", data=" + this.data + ", msg=" + this.msg + "]";
	}
}
