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
	private Integer userid;  //留言的发布人id
	private Integer goodsid;
	private Integer leaf;  //null 用来区分留言和回复

	@Transient
	private Object state;  //判断点赞
	@Transient
	private User user;
	@Transient
	private List<Reply> replyList;

}
