package com.sht.deal.utils;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
	private Long total; //总条数
	private Integer Pages; //总页数
	private List<T> data; //当前页数据

	public PageResult() {
	}

	public PageResult(Long total, List<T> data){
		this.total = total;
		this.data = data;
	}

	public PageResult(Long total, Integer Pages, List<T> data) {
		this.total = total;
		this.Pages = Pages;
		this.data = data;
	}
}
