package com.sht.deal.domain;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Data
public class Reply
{
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;
	private String createtime;
	private String content;
	private Integer number;
	private Integer leaf;   //等于0时就是一个树的末尾
	private Integer userid;  //回复的发布人id
	private Integer goodsid;  //商品的id
	private Integer commentid;
	private Integer nameid;  //给谁回复的人id 父id
	private Integer parentid;

	@Transient
	private String parentname;//父name
	@Transient
	private Object state;  //判断点赞
	@Transient
	private User user;
	@Transient
	private List<Reply> replyList;


}
