package com.sht.deal.Mapper;

import com.sht.deal.domain.Collect;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface CollectMapper extends Mapper<Collect> {
	@Select({"select * from collect where user_id = #{userId} and goods_id = #{goodsId}"})
	Collect findOne(Integer userId, Integer goodsId);
}
