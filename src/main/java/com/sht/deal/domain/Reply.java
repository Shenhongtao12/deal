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
	private Integer leaf;
	private Integer userid;
	private Integer goodsid;
	private Integer commentid;
	private Integer nameid;
	private String parentname;
	private Integer parentid;

	@Transient
	private Object state;
	@Transient
	private User user;
	@Transient
	private List<Reply> replyList;


}
