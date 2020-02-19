package com.sht.deal.Mapper;

import com.sht.deal.domain.Classify2;
import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface Classify2Mapper extends Mapper<Classify2> {
	@Select("select * from classify2 where classify1id = #{classify1id}")
	@Results({@Result(id = true, property = "id", column = "id"), @Result(property = "name", column = "name"), @Result(property = "image", column = "image"), @Result(property = "classify1id", column = "classify1id")})
	List<Classify2> findByClassId(int classify1id) throws Exception;
}
