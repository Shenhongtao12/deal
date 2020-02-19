package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Data
public class Comment {
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer commentid;
	private String createtime;
	private String content;
	private Integer number;
	private Integer userid;
	private Integer goodsid;
	private Integer leaf;

	@Transient
	private Object state;
	@Transient
	private User user;
	@Transient
	private List<Reply> replyList;

}
