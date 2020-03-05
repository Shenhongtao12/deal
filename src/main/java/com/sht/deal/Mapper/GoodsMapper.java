package com.sht.deal.Mapper;

import com.sht.deal.domain.Goods;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface GoodsMapper extends Mapper<Goods> {
	@Select({"select userid from goods where id = #{id}"})
	int findUserIdByGoodsId(int id);

	//查询以及分类下的二级分类id
	@Select("select id from classify2 where classify1id = #{id1}")
    int[] findIdsByClassify1(Integer id1);
}
