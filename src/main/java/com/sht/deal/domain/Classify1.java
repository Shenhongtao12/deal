package com.sht.deal.domain;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Data
public class Classify1 {
	@Id
	@KeySql(useGeneratedKeys = true)
	private Integer id;

	private String name;
	private String image;
	@Transient
	private List<Classify2> classify2List;

}
