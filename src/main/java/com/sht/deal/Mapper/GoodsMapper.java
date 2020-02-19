package com.sht.deal.Mapper;

import com.sht.deal.domain.Goods;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface GoodsMapper extends Mapper<Goods> {
	@Select({"select userid from goods where id = #{id}"})
	int findUserIdByGoodsId(int id);
}
