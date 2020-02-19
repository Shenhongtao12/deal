package com.sht.deal.Mapper;

import com.sht.deal.domain.Fans;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface FansMapper extends Mapper<Fans> {
    @Select("select * from fans where user_id = #{userId} and fans_id = #{fansId}")
    Fans findOne(Integer userId, Integer fansId);
}
