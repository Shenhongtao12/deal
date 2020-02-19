package com.sht.deal.Mapper;

import com.sht.deal.domain.Classify1;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface Classify1Mapper extends Mapper<Classify1> {

    @Select("select * from classify1 where id=#{id}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "image", column = "image"),
            @Result(property = "classify2List", column = "id", javaType = java.util.List.class, many = @Many(select = "com.sht.deal.Mapper.Classify2Mapper.findByClassId"))})
    Classify1 findChildById(int id) throws Exception;
}
